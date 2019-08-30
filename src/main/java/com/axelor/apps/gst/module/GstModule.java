package com.axelor.apps.gst.module;

import com.axelor.app.AxelorModule;
import com.axelor.apps.account.service.invoice.print.InvoicePrintServiceImpl;
import com.axelor.apps.base.service.AddressServiceImpl;
import com.axelor.apps.businessproject.service.InvoiceServiceProjectImpl;
import com.axelor.apps.gst.service.AddressServiceGstImpl;
import com.axelor.apps.gst.service.InvoiceLineGstService;
import com.axelor.apps.gst.service.InvoiceLineServiceGstImpl;
import com.axelor.apps.gst.service.InvoicePrintServiceGstImpl;
import com.axelor.apps.gst.service.InvoiceServiceGstImpl;
import com.axelor.apps.gst.service.PartnerServiceGstImpl;
import com.axelor.apps.gst.service.ProductInvoiceService;
import com.axelor.apps.gst.service.ProductInvoiceServiceImpl;
import com.axelor.apps.sale.service.PartnerSaleService;

public class GstModule extends AxelorModule {

  @Override
  protected void configure() {
    bind(InvoiceLineGstService.class).to(InvoiceLineServiceGstImpl.class);
    bind(PartnerSaleService.class).to(PartnerServiceGstImpl.class);
    bind(InvoiceServiceProjectImpl.class).to(InvoiceServiceGstImpl.class);
    bind(ProductInvoiceService.class).to(ProductInvoiceServiceImpl.class);
    bind(AddressServiceImpl.class).to(AddressServiceGstImpl.class);
    bind(InvoicePrintServiceImpl.class).to(InvoicePrintServiceGstImpl.class);
  }
}
