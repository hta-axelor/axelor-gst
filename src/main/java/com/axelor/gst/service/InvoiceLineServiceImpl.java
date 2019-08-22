package com.axelor.gst.service;

public class InvoiceLineServiceImpl implements InvoiceLineService {
  //
  //  @Override
  //  public InvoiceLine calculateAllItems(Invoice invoice, InvoiceLine invoiceLine) {
  //
  //    BigDecimal igst = BigDecimal.ZERO;
  //    BigDecimal cgst = BigDecimal.ZERO;
  //
  //    BigDecimal qty = invoiceLine.getQty();
  //    BigDecimal price = invoiceLine.getPrice();
  //    BigDecimal gstRate = invoiceLine.getGstRate();
  //
  //    BigDecimal netAmount = price.multiply(qty);
  //    invoiceLine.setNetAmount(netAmount);
  //
  //    State companyState = invoice.getCompany().getAddress().getState();
  //    State invoiceAddressState = invoice.getInvoiceAddress().getState();
  //
  //    if (companyState.equals(invoiceAddressState)) {
  //      cgst = netAmount.multiply(gstRate).divide(new BigDecimal(200));
  //    } else {
  //      igst = netAmount.multiply(gstRate).divide(new BigDecimal(100));
  //    }
  //    invoiceLine.setIgst(igst);
  //    invoiceLine.setSgst(cgst);
  //    invoiceLine.setCgst(cgst);
  //    BigDecimal grossAmount = netAmount.add(igst).add(cgst).add(cgst);
  //    invoiceLine.setGrossAmount(grossAmount);
  //    return invoiceLine;
  //  }
  //
  //  @Override
  //  public InvoiceLine setProductItems(InvoiceLine invoiceLine, Product product) {
  //    invoiceLine.setProduct(product);
  //    invoiceLine.setHsbn(product.getHsbn() == null ? "" : product.getHsbn());
  //    invoiceLine.setItem(
  //        product.getCategory() == null
  //            ? product.getCode()
  //            : product.getCategory().getName() + " :[" + product.getCode() + "]");
  //    invoiceLine.setPrice(product.getSalePrice());
  //    invoiceLine.setGstRate(product.getGstRate());
  //    return invoiceLine;
  //  }
}
