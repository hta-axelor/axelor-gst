package com.axelor.apps.gst.web;

import com.axelor.apps.base.db.repo.SequenceRepository;
import com.axelor.apps.base.service.administration.SequenceService;
import com.axelor.apps.gst.exception.IExceptionMessage;
import com.axelor.exception.AxelorException;
import com.axelor.exception.ResponseMessageType;
import com.axelor.exception.db.repo.TraceBackRepository;
import com.axelor.exception.service.TraceBackService;
import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Singleton;

@Singleton
public class StateController {
  public void getSequence(ActionRequest request, ActionResponse response) {
    try {
      String sequence =
          Beans.get(SequenceService.class).getSequenceNumber(SequenceRepository.STATE);
      if (sequence == null) {
        throw new AxelorException(
            TraceBackRepository.CATEGORY_CONFIGURATION_ERROR,
            I18n.get(IExceptionMessage.STATE_NO_SEQUENCE));
      } else {
        response.setValue("stateSeq", sequence);
      }

    } catch (AxelorException e) {
      TraceBackService.trace(response, e, ResponseMessageType.ERROR);
    }
  }
}
