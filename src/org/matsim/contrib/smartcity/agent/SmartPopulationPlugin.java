/**
 * 
 */
package org.matsim.contrib.smartcity.agent;

import java.util.Collection;
import java.util.Collections;

import org.matsim.core.config.Config;
import org.matsim.core.mobsim.qsim.PopulationPlugin;
import org.matsim.core.mobsim.qsim.agents.AgentFactory;
import org.matsim.core.mobsim.qsim.agents.PopulationAgentSource;
import com.google.inject.Module;

/**
 * Plugin for MATSim that bind the agent factory to SmartAgentFactory
 * 
 * @see SmartAgentFactory
 * @author Filippo Muzzini
 *
 */
public class SmartPopulationPlugin extends PopulationPlugin {

	
	public SmartPopulationPlugin(Config config) {
		super(config);
	}
	
	@Override
	public Collection<? extends Module> modules() {
		return Collections.singletonList(new com.google.inject.AbstractModule() {
			@Override
			protected void configure() {
				bind(PopulationAgentSource.class).asEagerSingleton();
				bind(AgentFactory.class).to(SmartAgentFactory.class).asEagerSingleton();
			}
		});
	}

}
