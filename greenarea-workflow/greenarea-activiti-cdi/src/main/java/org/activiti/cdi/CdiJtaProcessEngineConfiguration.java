/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.activiti.cdi;

import java.util.ArrayList;
import java.util.Collection;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.impl.cfg.JtaProcessEngineConfiguration;
import org.activiti.spring.autodeployment.AutoDeploymentStrategy;
import org.activiti.spring.autodeployment.DefaultAutoDeploymentStrategy;
import org.activiti.spring.autodeployment.ResourceParentFolderAutoDeploymentStrategy;
import org.activiti.spring.autodeployment.SingleResourceAutoDeploymentStrategy;
import org.springframework.core.io.Resource;

/**
 * @author Daniel Meyer
 */
public class CdiJtaProcessEngineConfiguration extends
		JtaProcessEngineConfiguration {

	protected String deploymentName = "SpringAutoDeployment";
	protected Resource[] deploymentResources = new Resource[0];
	protected String deploymentMode = "default";
	private Collection<AutoDeploymentStrategy> deploymentStrategies = new ArrayList<AutoDeploymentStrategy>();

	public CdiJtaProcessEngineConfiguration() {
		super();
		deploymentStrategies.add(new DefaultAutoDeploymentStrategy());
		deploymentStrategies.add(new SingleResourceAutoDeploymentStrategy());
		deploymentStrategies
				.add(new ResourceParentFolderAutoDeploymentStrategy());
	}

	@Override
	public ProcessEngine buildProcessEngine() {
		ProcessEngine processEngine = super.buildProcessEngine();
		ProcessEngines.setInitialized(true);
		autoDeployResources(processEngine);
		return processEngine;
	}

	@Override
	protected void initExpressionManager() {
		expressionManager = new CdiExpressionManager();
	}

	protected void autoDeployResources(ProcessEngine processEngine) {
		if (deploymentResources != null && deploymentResources.length > 0) {
			final AutoDeploymentStrategy strategy = getAutoDeploymentStrategy(deploymentMode);
			strategy.deployResources(deploymentName, deploymentResources,
					processEngine.getRepositoryService());
		}
	}

	public Resource[] getDeploymentResources() {
		return deploymentResources;
	}

	public void setDeploymentResources(Resource[] deploymentResources) {
		this.deploymentResources = deploymentResources;
	}

	protected AutoDeploymentStrategy getAutoDeploymentStrategy(final String mode) {
		AutoDeploymentStrategy result = new DefaultAutoDeploymentStrategy();
		for (final AutoDeploymentStrategy strategy : deploymentStrategies) {
			if (strategy.handlesMode(mode)) {
				result = strategy;
				break;
			}
		}
		return result;
	}
}
