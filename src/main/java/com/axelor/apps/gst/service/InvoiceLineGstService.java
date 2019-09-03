package com.axelor.apps.gst.service;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.account.db.TaxLine;
import com.axelor.exception.AxelorException;
import java.math.BigDecimal;

public interface InvoiceLineGstService {

  public void computeGstValues(Invoice invoice, InvoiceLine invoiceLine) throws AxelorException;

  public TaxLine getGstTaxLine(BigDecimal gstRate) throws AxelorException;
}
