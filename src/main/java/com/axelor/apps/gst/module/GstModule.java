package com.axelor.apps.gst.module;

import com.axelor.app.AxelorModule;
import com.axelor.apps.businessproject.service.InvoiceServiceProjectImpl;
import com.axelor.apps.gst.service.InvoiceLineServiceGstImpl;
import com.axelor.apps.gst.service.InvoiceServiceGstImpl;
import com.axelor.apps.gst.service.PartnerServiceGstImpl;
import com.axelor.apps.gst.service.InvoiceLineGstService;
import com.axelor.apps.sale.service.PartnerSaleService;
import com.axelor.apps.supplychain.service.InvoiceLineSupplychainService;

public class GstModule extends AxelorModule {

  @Override
  protected void configure() {
    bind(InvoiceLineGstService.class).to(InvoiceLineServiceGstImpl.class);
    bind(PartnerSaleService.class).to(PartnerServiceGstImpl.class);
    bind(InvoiceServiceProjectImpl.class).to(InvoiceServiceGstImpl.class);
  }
}
