package com.axelor.apps.gst.service;

import com.axelor.apps.base.db.Address;
import com.axelor.apps.base.db.Partner;
import com.axelor.apps.sale.service.PartnerSaleService;

public class PartnerServiceGstImpl extends PartnerSaleService {

  public Address getShippingAddress(Partner partner) {
    return getAddress(
        partner,
        "self.partner.id = ?1 AND self.isInvoicingAddr = false AND self.isDeliveryAddr = true AND self.isDefaultAddr = true",
        "self.partner.id = ?1 AND self.isDeliveryAddr = true");
  }
}
