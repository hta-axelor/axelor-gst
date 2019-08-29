package com.axelor.apps.gst.web;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.account.db.Tax;
import com.axelor.apps.account.db.TaxLine;
import com.axelor.apps.account.db.repo.TaxRepository;
import com.axelor.apps.account.service.invoice.InvoiceLineService;
import com.axelor.apps.gst.service.InvoiceLineGstService;
import com.axelor.exception.AxelorException;
import com.axelor.exception.service.TraceBackService;
import com.axelor.gst.db.State;
import com.axelor.inject.Beans;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.math.BigDecimal;
import java.util.List;

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
       invoiceLineGstService.computeGstValues(invoice,invoiceLine);
   	   response.setValues(invoiceLine);
    }
    catch(Exception e) {
    	TraceBackService.trace(response, e);
    }
  }
  
  public void getTaxLine(ActionRequest request, ActionResponse response)  throws AxelorException {
	  InvoiceLine invoiceLine = request.getContext().asType(InvoiceLine.class);
	  try {
	       invoiceLineGstService.getGstTaxLine(invoiceLine);
	   	   response.setValues(invoiceLine);
	    }
	    catch(Exception e) {
	    	TraceBackService.trace(response, e);
	    }
  }
}
