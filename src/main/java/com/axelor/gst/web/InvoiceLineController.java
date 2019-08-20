package com.axelor.gst.web;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.gst.service.InvoiceLineService;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class InvoiceLineController {

  @Inject private InvoiceLineService invoiceLineService;

  public void setAllItems(ActionRequest request, ActionResponse response) {
    Invoice invoice = request.getContext().getParent().asType(Invoice.class);
    InvoiceLine invoiceLine = request.getContext().asType(InvoiceLine.class);
    invoiceLineService.calculateAllItems(invoice, invoiceLine);
    response.setValues(invoiceLine);
  }
}
