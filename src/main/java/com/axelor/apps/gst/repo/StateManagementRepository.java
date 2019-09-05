package com.axelor.apps.gst.repo;

import com.axelor.apps.base.db.repo.SequenceRepository;
import com.axelor.apps.base.service.administration.SequenceService;
import com.axelor.apps.gst.db.State;
import com.axelor.apps.gst.db.repo.StateRepository;
import com.axelor.apps.gst.exception.IExceptionMessage;
import com.axelor.exception.AxelorException;
import com.axelor.exception.db.repo.TraceBackRepository;
import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;
import javax.persistence.PersistenceException;

public class StateManagementRepository extends StateRepository {

  @Override
  public State save(State entity) {
    try {
      if (entity.getStateSeq() == null) {
        String sequence =
            Beans.get(SequenceService.class).getSequenceNumber(SequenceRepository.STATE);
        if (sequence == null) {
          throw new AxelorException(
              TraceBackRepository.CATEGORY_CONFIGURATION_ERROR,
              I18n.get(IExceptionMessage.STATE_NO_SEQUENCE));
        }
        entity.setStateSeq(sequence);
      }
      return super.save(entity);
    } catch (Exception e) {
      throw new PersistenceException(e);
    }
  }
}
