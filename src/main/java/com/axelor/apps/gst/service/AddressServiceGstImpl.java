/*
 * Axelor Business Solutions
 *
 * Copyright (C) 2019 Axelor (<http://axelor.com>).
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License, version 3,
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.axelor.apps.gst.service;

import com.axelor.apps.base.db.Address;
import com.axelor.apps.base.service.AddressServiceImpl;
import com.google.inject.Singleton;

@Singleton
public class AddressServiceGstImpl extends AddressServiceImpl {

    @Override
    public String computeAddressStr(Address address) {
      StringBuilder addressString = new StringBuilder();
      if (address == null) {
        return "";
      }
  
      if (address.getAddressL2() != null) {
        addressString.append(address.getAddressL2()).append("\n");
      }
      if (address.getAddressL3() != null) {
        addressString.append(address.getAddressL3()).append("\n");
      }
      if (address.getAddressL4() != null) {
        addressString.append(address.getAddressL4()).append("\n");
      }
      if (address.getAddressL5() != null) {
        addressString.append(address.getAddressL5()).append("\n");
      }
      if (address.getAddressL6() != null) {
        addressString.append(address.getAddressL6());
      }
      if (address.getState() != null) {
        addressString.append(address.getState());
      }
      if (address.getAddressL7Country() != null) {
        addressString =
   addressString.append("\n").append(address.getAddressL7Country().getName());
      }
  
      return addressString.toString();
    }
}
