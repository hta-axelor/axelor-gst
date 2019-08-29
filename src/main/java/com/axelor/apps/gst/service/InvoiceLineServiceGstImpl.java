package com.axelor.apps.gst.service;

import java.math.BigDecimal;
import java.util.List;
import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.account.db.Tax;
import com.axelor.apps.account.db.TaxLine;
import com.axelor.apps.account.db.repo.TaxRepository;
import com.axelor.apps.base.exceptions.IExceptionMessage;
import com.axelor.exception.AxelorException;
import com.axelor.exception.db.repo.TraceBackRepository;
import com.axelor.gst.db.State;
import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;

public class InvoiceLineServiceGstImpl implements InvoiceLineGstService {
	
	@Override
	public void getGstTaxLine(InvoiceLine invoiceLine) throws AxelorException {
		BigDecimal gstRate = invoiceLine.getGstRate();
		Tax tax = Beans.get(TaxRepository.class).all().filter("self.code = ?1", "GST").fetchOne();
		if (tax == null) {
			throw new AxelorException(TraceBackRepository.CATEGORY_MISSING_FIELD, I18n.get(IExceptionMessage.TAX_2));
		}
		List<TaxLine> taxLineList = tax.getTaxLineList();
		for (TaxLine taxLine : taxLineList) {
			if (taxLine.getValue().equals(gstRate)) {
				invoiceLine.setTaxLine(taxLine);
				break;
			}
		}
	}

	@Override
	public void computeGstValues(Invoice invoice, InvoiceLine invoiceLine) throws AxelorException {
		BigDecimal gstRate = invoiceLine.getProduct().getGstRate();
		String hsbn = invoiceLine.getProduct().getHsbn();
		BigDecimal igst = BigDecimal.ZERO;
		BigDecimal cgst = BigDecimal.ZERO;
		BigDecimal exTaxTotal = invoiceLine.getExTaxTotal();

		if(invoice.getCompany().getAddress().getState() == null || invoice.getAddress().getState() == null) {
			throw new AxelorException(TraceBackRepository.CATEGORY_MISSING_FIELD, "Missing state in address");
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
