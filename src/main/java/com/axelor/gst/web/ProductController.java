package com.axelor.gst.web;

import com.axelor.app.AppSettings;
import com.axelor.apps.account.db.Invoice;
import com.axelor.meta.schema.actions.ActionView;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Singleton;
import java.util.List;

@Singleton
public class ProductController {

  public void setAttachmentPath(ActionRequest request, ActionResponse response) {
    String attachmentPath = AppSettings.get().get("file.upload.dir");
    request.getContext().put("AttachmentPath", attachmentPath + "/");
  }

  public void getInvoiceDetails(ActionRequest request, ActionResponse response) {
    List<String> productIdList = (List<String>) request.getContext().get("_ids");
    response.setView(
        ActionView.define("Invoice")
            .model(Invoice.class.getName())
            .add("form", "invoice-form-popup")
            .context("productIds", productIdList)
            .param("popup", "true")
            .param("show-toolbar", "false")
            .param("show-confirm", "false")
            .param("popup-save", "false")
            .map());
  }
}
