package com.axelor.gst.web;

import com.axelor.app.AppSettings;
import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.base.db.Company;
import com.axelor.apps.base.db.Sequence;
import com.axelor.apps.base.db.repo.CompanyRepository;
import com.axelor.gst.db.Party;
import com.axelor.gst.db.repo.PartyRepository;
import com.axelor.gst.repo.GstSequenceRepository;
import com.axelor.gst.service.InvoiceService;
import com.axelor.gst.service.SequenceService;
import com.axelor.inject.Beans;
import com.axelor.meta.schema.actions.ActionView;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.List;

@Singleton
public class InvoiceController {

  @Inject private InvoiceService invoiceService;

  @Inject private SequenceService sequenceService;

  public void setItems(ActionRequest request, ActionResponse response) {
    Invoice invoice = request.getContext().asType(Invoice.class);
    invoice = invoiceService.calculateItems(invoice);
    response.setValues(invoice);
  }

  public void setPartyValues(ActionRequest request, ActionResponse response) {
    Invoice invoice = request.getContext().asType(Invoice.class);
    invoice = invoiceService.setPartyValues(invoice);
    response.setValues(invoice);
  }

  public void setShippingAddress(ActionRequest request, ActionResponse response) {
    Invoice invoice = request.getContext().asType(Invoice.class);
    invoice = invoiceService.getShippingAddress(invoice);
    response.setValues(invoice);
  }

  public void setReference(ActionRequest request, ActionResponse response) {
    Invoice invoice = request.getContext().asType(Invoice.class);
    GstSequenceRepository sequenceRepository = Beans.get(GstSequenceRepository.class);
    Sequence sequence =
        sequenceRepository
            .all()
            .filter("self.metaModel.fullName = ?1", request.getModel())
            .fetchOne();
    if (sequence == null) {
      response.setError("No Sequence Found, Please enter the sequence");
      return;
    }
    String reference = invoice.getReference();
    if (reference == null) {
      response.setValue("reference", sequence.getNextNumber());
      sequenceService.computeReference(sequence);
    }
  }

  public void setAttachmentPath(ActionRequest request, ActionResponse response) {
    String attachmentPath = AppSettings.get().get("file.upload.dir");
    request.getContext().put("AttachmentPath", attachmentPath + "/");
  }

  public void createInvoice(ActionRequest request, ActionResponse response) {
    Invoice invoice = request.getContext().asType(Invoice.class);
    List<String> productIdList = (List<String>) request.getContext().get("productIds");

    // Checking Null States in Party
    try {
      invoiceService.checkCompanyNullStates(invoice);
      invoiceService.checkPartyNullStates(invoice);
    } catch (Exception e) {
      response.setError(e.getMessage());
      return;
    }
    response.setView(
        ActionView.define("Invoice")
            .model(Invoice.class.getName())
            .add("form", "invoice-form")
            .context("companyId", invoice.getCompany().getId())
            .context("partyId", invoice.getParty().getId())
            .context("product_ids", productIdList)
            .map());
  }

  public void setInvoiceDetails(ActionRequest request, ActionResponse response) {
    Invoice invoice = request.getContext().asType(Invoice.class);

    // Getting product id list from Product grid
    List<String> productIdList = (List<String>) request.getContext().get("product_ids");
    Integer companyId = (Integer) request.getContext().get("companyId");
    Integer partyId = (Integer) request.getContext().get("partyId");

    Company company = Beans.get(CompanyRepository.class).find(companyId.longValue());
    Party party = Beans.get(PartyRepository.class).find(partyId.longValue());

    invoice.setIsInvoiceAddress(true);

    // checking default company is selected
    if (company != null) {
      invoice.setCompany(company);
    }
    invoice.setParty(party);
    invoice = invoiceService.setPartyValues(invoice);
    invoice = invoiceService.getShippingAddress(invoice);
    invoice = invoiceService.calculateProductItemsList(invoice, productIdList);
    response.setValue("invoiceItemsList", request.getContext().get("product_invoice_items"));
    invoiceService.calculateItems(invoice);
    response.setValues(invoice);
  }
}
