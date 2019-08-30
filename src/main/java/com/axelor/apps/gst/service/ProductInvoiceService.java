package com.axelor.apps.gst.service;

import java.util.List;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.base.db.Product;
import com.axelor.exception.AxelorException;

public interface ProductInvoiceService {
   public Invoice createInvoice(Invoice invoice, List<Product> productList) throws AxelorException;
}
