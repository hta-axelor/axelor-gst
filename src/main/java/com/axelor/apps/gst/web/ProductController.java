package com.axelor.apps.gst.web;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.account.db.Tax;
import com.axelor.apps.account.db.TaxLine;
import com.axelor.apps.account.db.repo.InvoiceRepository;
import com.axelor.apps.account.db.repo.TaxRepository;
import com.axelor.apps.account.service.invoice.InvoiceLineService;
import com.axelor.apps.account.service.invoice.InvoiceService;
import com.axelor.apps.account.service.invoice.generator.InvoiceGenerator;
import com.axelor.apps.account.service.invoice.generator.InvoiceLineGenerator;
import com.axelor.apps.base.db.Address;
import com.axelor.apps.base.db.BankDetails;
import com.axelor.apps.base.db.Company;
import com.axelor.apps.base.db.Partner;
import com.axelor.apps.base.db.Product;
import com.axelor.apps.base.db.repo.PartnerRepository;
import com.axelor.apps.base.db.repo.PriceListRepository;
import com.axelor.apps.base.db.repo.ProductRepository;
import com.axelor.apps.base.exceptions.IExceptionMessage;
import com.axelor.apps.base.service.PartnerPriceListService;
import com.axelor.apps.base.service.PartnerService;
import com.axelor.apps.gst.service.InvoiceLineGstService;
import com.axelor.apps.gst.service.PartnerServiceGstImpl;
import com.axelor.apps.supplychain.service.app.AppSupplychainService;
import com.axelor.exception.AxelorException;
import com.axelor.exception.db.repo.TraceBackRepository;
import com.axelor.exception.service.TraceBackService;
import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;
import com.axelor.meta.schema.actions.ActionView;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;

@Singleton
@Transactional(rollbackOn = {AxelorException.class, Exception.class})
public class ProductController {

	@Inject	InvoiceRepository invoiceRepo;
	@Inject	PartnerServiceGstImpl partnerServiceGst;
	@Inject InvoiceService invoiceService;
	@Inject	InvoiceLineService invoiceLineService;
	@Inject private InvoiceLineGstService invoiceLineGstService;

	public void createInvoice(ActionRequest request, ActionResponse response) throws AxelorException {
		Invoice invoice = request.getContext().asType(Invoice.class);
		List<String> productIdList = (List<String>) request.getContext().get("product-ids");
		List<Product> productList = Beans.get(ProductRepository.class).all().filter("self.id IN ?1", productIdList)
				.fetch();
		Company company = invoice.getCompany();
		Partner partner = invoice.getPartner();
		List<BankDetails> bankDetailsList = partner.getBankDetailsList();
		BankDetails bankDetails = null;
		Partner contactPartner = null;
		Address mainInvoicingAddress = Beans.get(PartnerService.class).getInvoicingAddress(partner);

		for (BankDetails bankDetailsIt : bankDetailsList) {
			if (bankDetailsIt.getActive() || bankDetailsIt.getIsDefault()) {
				bankDetails = bankDetailsIt;
			}
		}

		Set<Partner> contactPartnerSet = partner.getContactPartnerSet();
		for (Partner contactPartnerIt : contactPartnerSet) {
			if (contactPartnerIt.getContactType().equals(PartnerRepository.CONTACT_PRIMARY)) {
				contactPartner = contactPartnerIt;
			}
		}

		// Checking null values in Wizard
		if (company.getAddress().getState() == null || mainInvoicingAddress == null
				|| mainInvoicingAddress.getState() == null) {
			try {
				throw new AxelorException(TraceBackRepository.CATEGORY_MISSING_FIELD, "Missing Address or State");
			} catch (AxelorException e) {
				TraceBackService.trace(response, e);
			}
		}

		InvoiceGenerator invoiceGenerator = new InvoiceGenerator(InvoiceRepository.OPERATION_TYPE_CLIENT_SALE, company,
				partner.getPaymentCondition(), partner.getInPaymentMode(), mainInvoicingAddress, partner,
				contactPartner, partner.getCurrency(),
				Beans.get(PartnerPriceListService.class).getDefaultPriceList(partner, PriceListRepository.TYPE_SALE),
				null, null, null, bankDetails, null) {

			@Override
			public Invoice generate() throws AxelorException {
				return super.createInvoiceHeader();
			}
		};
		
		invoice = invoiceGenerator.generate();
		invoice.setShippingAddress(partnerServiceGst.getShippingAddress(partner));
		invoice.setOperationTypeSelect(InvoiceRepository.OPERATION_TYPE_CLIENT_SALE);
		
		Tax tax = Beans.get(TaxRepository.class).all().filter("self.code = ?1", "GST").fetchOne();
		if (tax == null) {
			throw new AxelorException(TraceBackRepository.CATEGORY_MISSING_FIELD, I18n.get(IExceptionMessage.TAX_2));
		}
		List<TaxLine> taxLineList = tax.getTaxLineList();
		List<InvoiceLine> invoiceItemsList = new ArrayList<>();
		TaxLine taxLine = null;

		
		for (Product product : productList) {
			for (TaxLine taxLineIt : taxLineList) {
				if (taxLineIt.getValue().equals(product.getGstRate())) {
					taxLine = taxLineIt;
				}
			}
			InvoiceLineGenerator invoiceLineGenerator =
			          new InvoiceLineGenerator(
			              invoice,
			              product,
			              product.getName(),
			              product.getSalePrice(),
			              product.getSalePrice(),
			              product.getSalePrice(),
			              product.getDescription(),
			              new BigDecimal(1),
			              product.getUnit(),
			              taxLine,
			              InvoiceLineGenerator.DEFAULT_SEQUENCE,
			              new BigDecimal(0),
			              0,
			              null,
			              null,
			              false,
			              false,
			              0) {
				          @Override
				          public List<InvoiceLine> creates() throws AxelorException {

				            InvoiceLine invoiceLine = this.createInvoiceLine();
				            invoiceLineGstService.computeGstValues(invoice, invoiceLine);
				            List<InvoiceLine> invoiceLines = new ArrayList<InvoiceLine>();
				            invoiceLines.add(invoiceLine);

				            return invoiceLines;
				          }
				        };
				        invoiceItemsList.addAll(invoiceLineGenerator.creates());
		}
		invoice.setInvoiceLineList(invoiceItemsList);
		invoiceService.compute(invoice);
		invoiceRepo.save(invoice);
		if (invoice != null) {
			response.setCanClose(true);
			response.setView(ActionView.define(I18n.get("Invoice generated")).model(Invoice.class.getName())
					.add("form", "invoice-form").add("grid", "invoice-grid")
					.context("_showRecord", String.valueOf(invoice.getId()))
					.context("_operationTypeSelect", InvoiceRepository.OPERATION_TYPE_CLIENT_SALE)
					.context("todayDate", Beans.get(AppSupplychainService.class).getTodayDate()).map());
		}
	}
}