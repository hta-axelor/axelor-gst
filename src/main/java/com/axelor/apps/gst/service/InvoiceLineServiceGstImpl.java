package com.axelor.apps.gst.service;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.account.db.Tax;
import com.axelor.apps.account.db.TaxLine;
import com.axelor.apps.account.db.repo.TaxRepository;
import com.axelor.apps.base.service.app.AppBaseService;
import com.axelor.apps.gst.db.State;
import com.axelor.apps.gst.exception.IExceptionMessage;
import com.axelor.exception.AxelorException;
import com.axelor.exception.db.repo.TraceBackRepository;
import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;
import com.google.inject.persist.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class InvoiceLineServiceGstImpl implements InvoiceLineGstService {

  @Override
  @Transactional()
  public TaxLine getGstTaxLine(BigDecimal gstRate) throws AxelorException {
    Tax tax = Beans.get(TaxRepository.class).all().filter("self.code = ?1", "GST").fetchOne();
    if (tax == null) {
      throw new AxelorException(
          TraceBackRepository.CATEGORY_MISSING_FIELD, IExceptionMessage.NO_TAX);
    }
    List<TaxLine> taxLineList = tax.getTaxLineList();
    TaxLine taxLine = null;
    if (taxLineList.size() == 0) {
      taxLine = new TaxLine();
      taxLine.setTax(tax);
      taxLine.setValue(gstRate);
      taxLine.setStartDate(Beans.get(AppBaseService.class).getTodayDate());
      taxLineList.add(taxLine);
      tax.setTaxLineList(taxLineList);
      return taxLine;
    }

    for (TaxLine taxLineIt : taxLineList) {
      if (taxLineIt
          .getValue()
          .setScale(AppBaseService.DEFAULT_NB_DECIMAL_DIGITS, RoundingMode.HALF_UP)
          .equals(gstRate)) {
        return taxLineIt;
      }
    }
    return taxLine;
  }

  @Override
  public void computeGstValues(Invoice invoice, InvoiceLine invoiceLine) throws AxelorException {
    if (invoiceLine.getProduct() == null) {
      invoiceLine.setGstRate(null);
      invoiceLine.setHsbn(null);
      invoiceLine.setIgst(null);
      invoiceLine.setSgst(null);
      invoiceLine.setCgst(null);
      return;
    }
    BigDecimal gstRate = invoiceLine.getProduct().getGstRate();
    String hsbn = invoiceLine.getProduct().getHsbn();
    BigDecimal igst = BigDecimal.ZERO;
    BigDecimal cgst = BigDecimal.ZERO;
    BigDecimal exTaxTotal = invoiceLine.getExTaxTotal();

    if (invoice.getCompany().getAddress().getState() == null
        || invoice.getAddress().getState() == null) {
      throw new AxelorException(
          TraceBackRepository.CATEGORY_MISSING_FIELD, I18n.get(IExceptionMessage.NO_STATE));
    }

    State companyState = invoice.getCompany().getAddress().getState();
    State addressState = invoice.getAddress().getState();

    if (companyState.equals(addressState)) {
      cgst = exTaxTotal.multiply(gstRate).divide(new BigDecimal(200));
    } else {
      igst = exTaxTotal.multiply(gstRate).divide(new BigDecimal(100));
    }
    invoiceLine.setGstRate(gstRate);
    invoiceLine.setHsbn(hsbn);
    invoiceLine.setIgst(igst);
    invoiceLine.setSgst(cgst);
    invoiceLine.setCgst(cgst);
  }
}