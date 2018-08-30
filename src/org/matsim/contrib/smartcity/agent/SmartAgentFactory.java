/**
 * 
 */
package org.matsim.contrib.smartcity.agent;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Person;
import org.matsim.contrib.dynagent.DynAgent;
import org.matsim.contrib.dynagent.DynAgentLogic;
import org.matsim.contrib.parking.parkingsearch.search.ParkingSearchLogic;
import org.matsim.contrib.smartcity.InstantationUtils;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.mobsim.framework.MobsimAgent;
import org.matsim.core.mobsim.qsim.agents.AgentFactory;
import org.matsim.core.mobsim.qsim.interfaces.Netsim;

import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * This AgentFactory instantiate the parking and smart drive logic specified in the
 * configuration for the specific person; the create the agent.
 * 
 * @see SmartAgentLogic
 * @see SmartDriverLogic
 * @see ParkingSearchLogic
 * @see Injector
 * @author Filippo Muzzini
 *
 */
public class SmartAgentFactory implements AgentFactory {
	
	@Inject private Netsim simulation;
	@Inject private Injector inj;
	@Inject private EventsManager events;
	
	private static final String DRIVE_LOGIC_NAME = "drivelogic";
	private static final String PARKING_LOGIC_NAME = "parkinglogic";
	
	private String DEFAULT_DRIVE_LOGIC = "agent.StaticDriverLogic";
	private String DEFAULT_PARKING_LOGIC = "agent.NoParkingLogic";

	/* (non-Javadoc)
	 * @see org.matsim.core.mobsim.qsim.agents.AgentFactory#createMobsimAgentFromPerson(org.matsim.api.core.v01.population.Person)
	 */
	@Override
	public MobsimAgent createMobsimAgentFromPerson(Person p) {
		String driveLogicClass = (String) p.getAttributes().getAttribute(DRIVE_LOGIC_NAME);
		String parkingLogicClass = (String) p.getAttributes().getAttribute(PARKING_LOGIC_NAME);
		driveLogicClass = driveLogicClass != null ? driveLogicClass : DEFAULT_DRIVE_LOGIC;
		parkingLogicClass = parkingLogicClass != null ? parkingLogicClass : DEFAULT_PARKING_LOGIC;
		
		SmartDriverLogic smartLogic = (SmartDriverLogic) InstantationUtils.instantiateForName(inj, driveLogicClass);
		ParkingSearchLogic parkingLogic = (ParkingSearchLogic) InstantationUtils.instantiateForName(inj, parkingLogicClass);
		
		DynAgentLogic agentLogic = new SmartAgentLogic(p.getSelectedPlan(), this.simulation, smartLogic, parkingLogic);
		inj.injectMembers(agentLogic);
		
		Id<Link> startLinkId = ((Activity) p.getSelectedPlan().getPlanElements().get(0)).getLinkId();
		
		return new DynAgent(p.getId(), startLinkId, events, agentLogic);
	}

}
