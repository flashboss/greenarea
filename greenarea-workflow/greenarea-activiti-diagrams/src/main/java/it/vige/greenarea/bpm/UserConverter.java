/******************************************************************************
 * Vige, Home of Professional Open Source Copyright 2010, Vige, and           *
 * individual contributors by the @authors tag. See the copyright.txt in the  *
 * distribution for a full listing of individual contributors.                *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may    *
 * not use this file except in compliance with the License. You may obtain    *
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0        *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 ******************************************************************************/
package it.vige.greenarea.bpm;

import it.vige.greenarea.dto.GreenareaUser;

import java.util.List;

import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;

public class UserConverter {

	public static GreenareaUser convertToGreenareaUser(User user) {
		return new GreenareaUser(user.getId(), user.getFirstName(),
				user.getLastName(), user.getEmail(), user.getPassword());
	}

	public boolean isUserInGroup(List<Group> groups, String groupToFind) {
		for (Group group : groups) {
			if (group.getId().equals(groupToFind))
				return true;
		}
		return false;
	}

}
