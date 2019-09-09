package com.axelor.csv.script;

import java.util.Map;
import com.axelor.apps.base.db.City;
import com.axelor.exception.AxelorException;
import com.google.inject.persist.Transactional;

public class ImportData {
	  @Transactional
	  public Object importCity(Object bean, Map<String, Object> values) throws AxelorException {
	    assert bean instanceof City;
	    City city = (City) bean;
        city.setCountry(city.getState().getCountry());
	    return city;
	  }
}
