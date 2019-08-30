package com.axelor.apps.gst.service;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.exception.AxelorException;

public interface InvoiceLineGstService {

    public void computeGstValues(Invoice invoice, InvoiceLine invoiceLine) throws AxelorException;
  
    public void getGstTaxLine(InvoiceLine invoiceLine) throws AxelorException;
}
