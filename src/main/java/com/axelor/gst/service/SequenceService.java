package com.axelor.gst.service;

import com.axelor.apps.base.db.Sequence;

public interface SequenceService {
  public Sequence computeNextSequence(Sequence sequence);

  public void computeReference(Sequence sequence);
}
