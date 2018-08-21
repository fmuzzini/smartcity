/**
 * 
 */
package org.matsim.contrib.smartcity.agent;

import org.matsim.api.core.v01.population.Person;
import org.matsim.core.config.Config;
import org.matsim.core.mobsim.framework.MobsimAgent;
import org.matsim.core.mobsim.qsim.agents.AgentFactory;
import org.matsim.core.mobsim.qsim.interfaces.Netsim;

import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * Agent Factory that instantiates and feed the fields of agnet's logic's instance,
 * then instantiates a SmartPersonDriverAgentImpl with previous logic.
 * 
 * @see SmartPersonDriverAgentImpl
 * @see SmartDriverLogic
 * @see Injector
 * @author Filippo Muzzini
 *
 */
public class SmartAgentFactory implements AgentFactory {
	
	@Inject private Config config;
	@Inject private Netsim simulation;
	@Inject private Injector inj;

	/* (non-Javadoc)
	 * @see org.matsim.core.mobsim.qsim.agents.AgentFactory#createMobsimAgentFromPerson(org.matsim.api.core.v01.population.Person)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public MobsimAgent createMobsimAgentFromPerson(Person p) {
		//TODO da tirare fuori dal config
		String smartClassString = "org.matsim.contrib.smartcity.agent.StaticDriverLogic";
		Class<? extends SmartDriverLogic> smartClass = null;
		try {
			smartClass = (Class<? extends SmartDriverLogic>) Class.forName(smartClassString);
		} catch (ClassNotFoundException e1) {
			System.err.println("Class "+smartClassString+" not found");
			e1.printStackTrace();
		}
		SmartDriverLogic smartLogic = null;
		try {
			//create a new instance
			smartLogic = smartClass.newInstance();
			//feed the fields
			inj.injectMembers(smartLogic);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		MobsimAgent agent = new SmartPersonDriverAgentImpl(p.getSelectedPlan(), this.simulation, smartLogic);		
		return agent;
	}

}
