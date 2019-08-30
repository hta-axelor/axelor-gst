package com.axelor.apps.gst.web;

import java.util.List;
import com.axelor.apps.ReportFactory;
import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.repo.InvoiceRepository;
import com.axelor.apps.base.db.Product;
import com.axelor.apps.base.db.repo.ProductRepository;
import com.axelor.apps.base.service.user.UserService;
import com.axelor.apps.gst.report.IReport;
import com.axelor.apps.gst.service.ProductInvoiceService;
import com.axelor.apps.supplychain.service.app.AppSupplychainService;
import com.axelor.auth.db.User;
import com.axelor.exception.AxelorException;
import com.axelor.exception.service.TraceBackService;
import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;
import com.axelor.meta.schema.actions.ActionView;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.common.base.Joiner;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ProductController {

	@Inject
	ProductInvoiceService productInvoiceService;

	@SuppressWarnings("unchecked")
	public void createInvoice(ActionRequest request, ActionResponse response) {
		Invoice invoice = request.getContext().asType(Invoice.class);
		List<String> productIdList = (List<String>) request.getContext().get("product-ids");
		List<Product> productList = Beans.get(ProductRepository.class).all().filter("self.id IN ?1", productIdList).fetch();
		try {
			invoice = productInvoiceService.createInvoice(invoice, productList);
			if (invoice != null) {
				response.setCanClose(true);
				response.setView(ActionView.define(I18n.get("Invoice generated")).model(Invoice.class.getName())
						.add("form", "invoice-form").add("grid", "invoice-grid")
						.context("_showRecord", String.valueOf(invoice.getId()))
						.context("_operationTypeSelect", InvoiceRepository.OPERATION_TYPE_CLIENT_SALE)
						.context("todayDate", Beans.get(AppSupplychainService.class).getTodayDate()).map());
			}
		} catch (AxelorException e) {
			TraceBackService.trace(response, e);
		}
	}
	
    @SuppressWarnings("unchecked")
	public void printProductDetails(ActionRequest request, ActionResponse response) throws AxelorException {
		    String productIds = "";
		    
		    User user = Beans.get(UserService.class).getUser();
			List<Integer> SelectedProductList = (List<Integer>) request.getContext().get("_ids");
		    if (SelectedProductList != null) {
		      productIds = Joiner.on(",").join(SelectedProductList);
		    }
		    String name = "Product Details";
		    String fileLink =
		        ReportFactory.createReport(IReport.PRODUCT_DETAILS, name + "-${date}").addParam("UserId", user.getId())
		            .addParam("ProductIds", productIds).generate().getFileLink();

		    response.setView(ActionView.define(name).add("html", fileLink).map());
	}
}
