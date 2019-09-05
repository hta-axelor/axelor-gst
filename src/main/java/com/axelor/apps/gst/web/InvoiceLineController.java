package com.axelor.apps.gst.web;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.account.db.TaxLine;
import com.axelor.apps.gst.service.InvoiceLineGstService;
import com.axelor.exception.AxelorException;
import com.axelor.exception.service.TraceBackService;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class InvoiceLineController {

  @Inject private InvoiceLineGstService invoiceLineGstService;

  public void computeGstValues(ActionRequest request, ActionResponse response)
      throws AxelorException {
    InvoiceLine invoiceLine = request.getContext().asType(InvoiceLine.class);
    Invoice invoice = invoiceLine.getInvoice();
    if (invoice == null) {
      invoice = request.getContext().getParent().asType(Invoice.class);
    }
    try {
      invoiceLineGstService.computeGstValues(invoice, invoiceLine);
      response.setValues(invoiceLine);
    } catch (AxelorException e) {
      TraceBackService.trace(response, e);
    }
  }

  public void getTaxLine(ActionRequest request, ActionResponse response) {
    InvoiceLine invoiceLine = request.getContext().asType(InvoiceLine.class);
    try {
      TaxLine taxLine = invoiceLineGstService.getGstTaxLine(invoiceLine.getGstRate());
      if (taxLine != null) {
        invoiceLine.setTaxLine(taxLine);
        response.setValue("taxLine", taxLine);
      }
    } catch (AxelorException e) {
      TraceBackService.trace(response, e);
    }
  }
}
