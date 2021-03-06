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

import static it.vige.greenarea.Constants.ANONYMOUS;
import static it.vige.greenarea.Constants.AUTISTA;
import static it.vige.greenarea.Constants.GUIDONIA;
import static it.vige.greenarea.Constants.LIVORNO;
import static it.vige.greenarea.Constants.OPERATORE_LOGISTICO;
import static it.vige.greenarea.Constants.PA;
import static it.vige.greenarea.Constants.POMEZIA;
import static it.vige.greenarea.Constants.SOCIETA_DI_TRASPORTO;
import static it.vige.greenarea.Constants.TRASPORTATORE_AUTONOMO;
import static java.util.Arrays.asList;
import static org.activiti.engine.impl.util.IoUtil.readInputStream;

import java.util.List;

import org.activiti.engine.ActivitiObjectNotFoundException;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.Picture;
import org.activiti.engine.identity.User;
import org.activiti.engine.repository.Deployment;

public class GreenareaDemoData {

	public void createUser(IdentityService identityService, String userId, String firstName, String lastName,
			String password, String email, String imageResource, List<String> groups, List<String> userInfo) {

		if (identityService.createUserQuery().userId(userId).count() == 0) {

			// Following data can already be set by demo setup script

			User user = identityService.newUser(userId);
			user.setFirstName(firstName);
			user.setLastName(lastName);
			user.setPassword(password);
			user.setEmail(email);
			identityService.saveUser(user);

			if (groups != null) {
				for (String group : groups) {
					identityService.createMembership(userId, group);
				}
			}
		}

		// Following data is not set by demo setup script

		// image
		if (imageResource != null) {
			byte[] pictureBytes = readInputStream(this.getClass().getClassLoader().getResourceAsStream(imageResource),
					null);
			Picture picture = new Picture(pictureBytes, "image/jpeg");
			identityService.setUserPicture(userId, picture);
		}

		// user info
		if (userInfo != null) {
			for (int i = 0; i < userInfo.size(); i += 2) {
				identityService.setUserInfo(userId, userInfo.get(i), userInfo.get(i + 1));
			}
		}

	}

	public void createGroup(IdentityService identityService, String groupId, String type) {
		if (identityService.createGroupQuery().groupId(groupId).count() == 0) {
			Group newGroup = identityService.newGroup(groupId);
			newGroup.setName(groupId.substring(0, 1).toUpperCase() + groupId.substring(1));
			newGroup.setType(type);
			identityService.saveGroup(newGroup);
		}
	}

	public void initDemoUsers(IdentityService identityService) {
		createUser(identityService, ANONYMOUS, "Anon", "Ymous", ANONYMOUS, "anonymous@vige.it",
				"org/activiti/explorer/images/fozzie.jpg", asList("user"), null);
		createUser(identityService, "amministratore", "Luca", "Stancapiano", "vulit", "greenareavige@gmail.com",
				"org/activiti/explorer/images/kermit.jpg", asList("user", "admin"),
				asList("birthDate", "15-05-1976", "jobTitle", "Consulente", "location", GUIDONIA, "phone",
						"+393381584484", "twitterName", "flashboss", "skype", "flashboss62"));
		createUser(identityService, "dhl", "Frodo", "Baggins", "vulit", "frodobaggins@vige.it",
				"org/activiti/explorer/images/fozzie.jpg", asList(OPERATORE_LOGISTICO, "user"), null);
		identityService.setUserInfo("dhl", "creditoMobilita", "1000");
		createUser(identityService, "tnt", "Bilbo", "Baggins", "vulit", "bilbobaggins@vige.it",
				"org/activiti/explorer/images/fozzie.jpg", asList(OPERATORE_LOGISTICO, "user"), null);
		identityService.setUserInfo("tnt", "creditoMobilita", "1000");
		createUser(identityService, "paguidonia", "Gan", "Dalf", "vulit", "gandalf@vige.it",
				"org/activiti/explorer/images/fozzie.jpg", asList(PA, GUIDONIA, "user"), null);
		createUser(identityService, "palivorno", "Ara", "Gorn", "vulit", "aragorne@vige.it",
				"org/activiti/explorer/images/kermit.jpg", asList(PA, LIVORNO, "user"), null);
		createUser(identityService, "papomezia", "Boro", "Mir", "vulit", "boromir@vige.it",
				"org/activiti/explorer/images/kermit.jpg", asList(PA, POMEZIA, "user"), null);
		createUser(identityService, "buscar", "Cele", "Born", "vulit", "celeborn@vige.it",
				"org/activiti/explorer/images/gonzo.jpg", asList(SOCIETA_DI_TRASPORTO, "user"), null);
		createUser(identityService, "trambus", "Dene", "Thor", "vulit", "denethor@vige.it",
				"org/activiti/explorer/images/gonzo.jpg", asList(SOCIETA_DI_TRASPORTO, "user"), null);
		createUser(identityService, "4006944", "Camillo", "Cinque", "vulit", "camillo.cinque@vige.it",
				"org/activiti/explorer/images/kermit.jpg", asList(SOCIETA_DI_TRASPORTO, "user"), null);
		createUser(identityService, "trasportatoreautonomo1", "Tra", "Sportatore", "vulit",
				"trasportatoreautonomo1@vige.it", "org/activiti/explorer/images/gonzo.jpg",
				asList(TRASPORTATORE_AUTONOMO, AUTISTA, "user"), null);
		createUser(identityService, "trasportatoreautonomo2", "Traspor", "Tatore", "vulit",
				"trasportatoreautonomo2@vige.it", "org/activiti/explorer/images/gonzo.jpg",
				asList(TRASPORTATORE_AUTONOMO, AUTISTA, "user"), null);
		createUser(identityService, "trasportatoreautonomo3", "Trasportato", "Re", "vulit",
				"trasportatoreautonomo3@vige.it", "org/activiti/explorer/images/gonzo.jpg",
				asList(TRASPORTATORE_AUTONOMO, AUTISTA, "user"), null);
		createUser(identityService, "autista1", "Auti", "Sta", "psw1", "autista1@vige.it",
				"org/activiti/explorer/images/gonzo.jpg", asList(AUTISTA, "user"), null);
		createUser(identityService, "autista2", "Au", "Tista", "vulit", "autista2@vige.it",
				"org/activiti/explorer/images/gonzo.jpg", asList(AUTISTA, "user"), null);
		createUser(identityService, "autista3", "Au3", "Tista3", "vulit", "autista3@vige.it",
				"org/activiti/explorer/images/gonzo.jpg", asList(AUTISTA, "user"), null);
		createUser(identityService, "autista4", "Au4", "Tista4", "vulit", "autista4@vige.it",
				"org/activiti/explorer/images/gonzo.jpg", asList(AUTISTA, "user"), null);
		createUser(identityService, "autista5", "Au5", "Tista5", "vulit", "autista5@vige.it",
				"org/activiti/explorer/images/gonzo.jpg", asList(AUTISTA, "user"), null);
		createUser(identityService, "autista6", "Au6", "Tista6", "vulit", "autista6@vige.it",
				"org/activiti/explorer/images/gonzo.jpg", asList(AUTISTA, "user"), null);
		createUser(identityService, "autista7", "Au7", "Tista7", "vulit", "autista7@vige.it",
				"org/activiti/explorer/images/gonzo.jpg", asList(AUTISTA, "user"), null);
		createUser(identityService, "autista8", "Au8", "Tista8", "vulit", "autista8@vige.it",
				"org/activiti/explorer/images/gonzo.jpg", asList(AUTISTA, "user"), null);
		createUser(identityService, "enzo1", "Walter", "Conti", "conti", "walter.conti@vige.it",
				"org/activiti/explorer/images/gonzo.jpg", asList(AUTISTA, "user"), null);
	}

	public void initDemoGroups(IdentityService identityService) {
		String[] assignmentGroups = new String[] { PA, LIVORNO, GUIDONIA, POMEZIA, OPERATORE_LOGISTICO,
				SOCIETA_DI_TRASPORTO, TRASPORTATORE_AUTONOMO, AUTISTA };
		for (String groupId : assignmentGroups) {
			createGroup(identityService, groupId, "assignment");
		}

		String[] securityGroups = new String[] { "user", "admin" };
		for (String groupId : securityGroups) {
			createGroup(identityService, groupId, "security-role");
		}
	}

	public void deleteAllIdentities(IdentityService identityService) {
		List<User> users = identityService.createUserQuery().list();
		for (User user : users) {
			identityService.deleteUser(user.getId());
		}
		List<Group> groups = identityService.createGroupQuery().list();
		for (Group group : groups) {
			identityService.deleteGroup(group.getId());
		}
	}

	public void deleteAllHistories(HistoryService historyService) {
		List<HistoricProcessInstance> historicInstances = historyService.createHistoricProcessInstanceQuery().list();
		for (HistoricProcessInstance historicProcessInstance : historicInstances)
			try {
				historyService.deleteHistoricProcessInstance(historicProcessInstance.getId());
			} catch (ActivitiObjectNotFoundException ex) {

			}
	}

	public void deleteAllIDeployments(RepositoryService repositoryService) {
		List<Deployment> deployments = repositoryService.createDeploymentQuery().list();
		for (Deployment deployment : deployments) {
			repositoryService.deleteDeployment(deployment.getId());
		}
	}

}
