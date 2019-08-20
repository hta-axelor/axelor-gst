package com.axelor.gst.repo;

import com.axelor.apps.base.db.Sequence;
import com.axelor.apps.base.db.repo.SequenceBaseRepository;
import com.axelor.gst.service.SequenceService;
import com.google.inject.Inject;

public class GstSequenceRepository extends SequenceBaseRepository {

  @Inject SequenceService sequenceService;

  @Override
  public Sequence save(Sequence entity) {
    if (entity.getNextNumber() == null) {
      entity = sequenceService.computeNextSequence(entity);
    }
    return super.save(entity);
  }
}
