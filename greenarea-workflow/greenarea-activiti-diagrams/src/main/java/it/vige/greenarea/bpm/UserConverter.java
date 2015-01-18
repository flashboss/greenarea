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
