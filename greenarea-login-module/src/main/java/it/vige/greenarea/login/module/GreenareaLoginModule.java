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
		return "prova";
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
