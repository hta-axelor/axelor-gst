package com.axelor.gst.service;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.base.db.Address;
import com.axelor.apps.base.db.Product;
import com.axelor.apps.base.db.repo.AddressRepository;
import com.axelor.apps.base.db.repo.ProductRepository;
import com.axelor.gst.db.Contact;
import com.axelor.gst.db.repo.ContactRepository;
import com.axelor.inject.Beans;
import com.google.inject.Inject;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class InvoiceServiceImpl implements InvoiceService {

  @Inject private InvoiceLineService invoiceLineService;

  @Override
  public Invoice calculateItems(Invoice invoice) {
    BigDecimal netAmountSum = BigDecimal.ZERO;
    BigDecimal gstRateSum = BigDecimal.ZERO;
    BigDecimal igstSum = BigDecimal.ZERO;
    BigDecimal cgstSum = BigDecimal.ZERO;
    BigDecimal grossAmount = BigDecimal.ZERO;

    List<InvoiceLine> invoiceLineList = invoice.getInvoiceItemsList();
    for (InvoiceLine il : invoiceLineList) {
      netAmountSum = netAmountSum.add(il.getNetAmount());
      gstRateSum = gstRateSum.add(il.getGstRate());
      igstSum = igstSum.add(il.getIgst());
      cgstSum = cgstSum.add(il.getCgst());
      grossAmount = grossAmount.add(il.getGrossAmount());
    }
    invoice.setNetAmount(netAmountSum);
    invoice.setNetIgst(igstSum);
    invoice.setNetCgst(cgstSum);
    invoice.setNetSgst(cgstSum);
    invoice.setGrossAmount(grossAmount);

    return invoice;
  }

  @Override
  public Invoice setPartyValues(Invoice invoice) {

    invoice.setPartyContact(null);
    invoice.setInvoiceAddress(null);
    invoice.setShippingAddress(null);

    // No Party found
    if (invoice.getParty() == null) {
      return invoice;
    }

    List<Contact> partyContactList = getPartyContactList(invoice);

    for (Contact contact : partyContactList) {
      // finding primary contact
      if (contact.getType().equals(ContactRepository.CONTACT_PRIMARY)) {
        invoice.setPartyContact(contact);
        break;
      }
    }

    List<Address> partyAddressList = getPartyAddressList(invoice);
    for (Address address : partyAddressList) {
      // finding invoice address && default address
      if (address.getType().equals(AddressRepository.ADDRESS_INVOICE)
          || address.getType().equals(AddressRepository.ADDRESS_DEFAULT)) {
        invoice.setInvoiceAddress(address);
        break;
      }
    }
    return invoice;
  }

  @Override
  public Invoice getShippingAddress(Invoice invoice) {

    invoice.setShippingAddress(null);

    List<Address> partyAddressList = getPartyAddressList(invoice);

    // Checking Flag
    if (invoice.getIsInvoiceAddress()) {
      invoice.setShippingAddress(invoice.getInvoiceAddress());
    } else {
      for (Address address : partyAddressList) {
        // finding shipping address && default address
        if (address.getType().equals(AddressRepository.ADDRESS_SHIPPING)
            || address.getType().equals(AddressRepository.ADDRESS_DEFAULT)) {
          invoice.setShippingAddress(address);
          break;
        }
      }
    }
    return invoice;
  }

  protected List<Contact> getPartyContactList(Invoice invoice) {
    List<Contact> partyContactList = new ArrayList<>();
    if (invoice.getParty() != null) {
      partyContactList = invoice.getParty().getContactList();
    }
    return partyContactList;
  }

  protected List<Address> getPartyAddressList(Invoice invoice) {
    List<Address> partyAddressList = new ArrayList<>();
    if (invoice.getParty() != null) {
      partyAddressList = invoice.getParty().getAddressList();
    }
    return partyAddressList;
  }

  @Override
  public Invoice calculateProductItemsList(Invoice invoice, List<String> productIdList) {
    List<Product> productList =
        Beans.get(ProductRepository.class).all().filter("self.id IN ?1", productIdList).fetch();
    List<InvoiceLine> invoiceItemsList = new ArrayList<>();
    for (Product product : productList) {
      InvoiceLine invoiceLine = new InvoiceLine();
      invoiceLine = invoiceLineService.setProductItems(invoiceLine, product);
      invoiceLine = invoiceLineService.calculateAllItems(invoice, invoiceLine);
      invoiceItemsList.add(invoiceLine);
    }
    invoice.setInvoiceItemsList(invoiceItemsList);

    return invoice;
  }

  @Override
  public void checkPartyNullStates(Invoice invoice) throws Exception {
    Boolean isShippingOrDefault = false;

    if (invoice.getParty().getAddressList().isEmpty()) {
      throw new Exception("Please enter address in party");
    }

    List<Address> addressList = invoice.getParty().getAddressList();
    for (Address address : addressList) {
      if (address.getType().equals(AddressRepository.ADDRESS_SHIPPING)
          || address.getType().equals(AddressRepository.ADDRESS_DEFAULT)) {
        isShippingOrDefault = true;
        if (address.getState() == null) {
          throw new Exception("Please enter state in party address");
        }
      }
    }
    if (!isShippingOrDefault) {
      throw new Exception("Please enter shipping address or invoice address");
    }
  }

  @Override
  public void checkCompanyNullStates(Invoice invoice) throws Exception {
    if (invoice.getCompany().getAddress() == null) {
      throw new Exception("Please enter address in company");
    }

    if (invoice.getCompany().getAddress().getState() == null) {
      throw new Exception("Please enter state in company address");
    }
  }
}
