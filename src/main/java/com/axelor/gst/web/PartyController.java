package com.axelor.gst.web;

import com.axelor.apps.base.db.Sequence;
import com.axelor.gst.repo.GstSequenceRepository;
import com.axelor.gst.service.SequenceService;
import com.axelor.inject.Beans;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class PartyController {

  @Inject private SequenceService sequenceService;

  public void setReference(ActionRequest request, ActionResponse response) {
    GstSequenceRepository sequenceRepository = Beans.get(GstSequenceRepository.class);
    Sequence sequence =
        sequenceRepository
            .all()
            .filter("self.metaModel.fullName = ?1", request.getModel())
            .fetchOne();
    if (sequence == null) {
      response.setError("No Sequence Found, Please enter the sequence");
      return;
    }
    response.setValue("reference", sequence.getNextNumber());
    sequenceService.computeReference(sequence);
  }
}
