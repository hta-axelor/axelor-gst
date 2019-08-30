package com.axelor.apps.gst.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import com.axelor.apps.base.exceptions.IExceptionMessage;
import com.axelor.apps.base.service.app.AppBaseService;
import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.account.db.Tax;
import com.axelor.apps.account.db.TaxLine;
import com.axelor.apps.account.db.repo.TaxRepository;
import com.axelor.apps.gst.db.State;
import com.axelor.exception.AxelorException;
import com.axelor.exception.db.repo.TraceBackRepository;
import com.axelor.inject.Beans;
import com.axelor.i18n.I18n;

public class InvoiceLineServiceGstImpl implements InvoiceLineGstService {

    @Override
    public void getGstTaxLine(InvoiceLine invoiceLine) throws AxelorException {
      BigDecimal gstRate = invoiceLine.getGstRate();
      Tax tax = Beans.get(TaxRepository.class).all().filter("self.code = ?1", "GST").fetchOne();
      if (tax == null) {
        throw new AxelorException(
            TraceBackRepository.CATEGORY_MISSING_FIELD, I18n.get(IExceptionMessage.TAX_2));
      }
      List<TaxLine> taxLineList = tax.getTaxLineList();
      TaxLine taxLine = null;
      if(taxLineList.size() == 0) {
		  taxLine = new TaxLine();
    	  taxLine.setTax(tax);
    	  taxLine.setValue(gstRate);
    	  taxLine.setStartDate(Beans.get(AppBaseService.class).getTodayDate());
      }  
      for (TaxLine taxLineIt : taxLineList) {
    	  if (taxLineIt.getValue().setScale(AppBaseService.DEFAULT_NB_DECIMAL_DIGITS, RoundingMode.HALF_UP).equals(gstRate)) {
          taxLine = taxLineIt;
          break;
        }
      }
      invoiceLine.setTaxLine(taxLine);
    }
  
    @Override
    public void computeGstValues(Invoice invoice, InvoiceLine invoiceLine) throws AxelorException
   {
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
            TraceBackRepository.CATEGORY_MISSING_FIELD, "Missing state in address");
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
