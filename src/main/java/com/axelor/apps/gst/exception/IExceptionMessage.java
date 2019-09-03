package com.axelor.apps.gst.exception;

public interface IExceptionMessage {
  /** STATE SEQUENCE */
  String STATE_NO_SEQUENCE = /* $$( */ "Error : You must configure a sequence for state" /* ) */;

  /** MISSING STATE IN ADDRESS */
  String NO_STATE = /* $$( */ "Error : Missing state in Address" /* ) */;

  /** MISSING TAX */
  public static final String NO_TAX = /*$$(*/ "Tax is missing" /*)*/;
}
