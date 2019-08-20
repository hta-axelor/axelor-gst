package com.axelor.gst.service;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.base.db.Product;

public interface InvoiceLineService {
  public InvoiceLine calculateAllItems(Invoice invoice, InvoiceLine invoiceLine);

  public InvoiceLine setProductItems(InvoiceLine invoiceLine, Product product);
}
