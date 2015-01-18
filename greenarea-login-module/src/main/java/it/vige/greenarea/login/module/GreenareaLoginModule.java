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
package it.vige.greenarea.login.module;

import static java.lang.reflect.Array.get;
import static java.lang.reflect.Array.getLength;
import static java.util.Arrays.asList;

import java.security.Principal;
import java.security.acl.Group;
import java.util.Enumeration;

import javax.security.auth.login.LoginException;

import org.jboss.security.auth.spi.UsernamePasswordLoginModule;

public class GreenareaLoginModule extends UsernamePasswordLoginModule {

	@Override
	protected String getUsersPassword() throws LoginException {
		return "vulit";
	}

	@Override
	protected Group[] getRoleSets() throws LoginException {
		return new Group[] { new Group() {

			@Override
			public String getName() {
				return "pa";
			}

			@Override
			public boolean addMember(Principal user) {
				return true;
			}

			@Override
			public boolean removeMember(Principal user) {
				return true;
			}

			@Override
			public boolean isMember(Principal member) {
				return true;
			}

			@Override
			public Enumeration<? extends Principal> members() {

				final Principal[] members = asList(new Principal() {

					@Override
					public String getName() {
						return "patorino";
					}
				}, new Principal() {

					@Override
					public String getName() {
						return "pagenova";
					}
				}, new Principal() {

					@Override
					public String getName() {
						return "pamilano";
					}
				}).toArray(new Principal[0]);
				return new Enumeration<Principal>() {
					int size = getLength(members);

					int cursor;

					public boolean hasMoreElements() {
						return (cursor < size);
					}

					public Principal nextElement() {
						return (Principal) get(members, cursor++);
					}
				};
			}

		} };
	}

}
