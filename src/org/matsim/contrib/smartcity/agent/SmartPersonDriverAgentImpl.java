/**
 * 
 */
package org.matsim.contrib.smartcity.agent;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.core.mobsim.framework.HasPerson;
import org.matsim.core.mobsim.framework.MobsimDriverAgent;
import org.matsim.core.mobsim.framework.MobsimPassengerAgent;
import org.matsim.core.mobsim.framework.PlanAgent;
import org.matsim.core.mobsim.qsim.agents.HasModifiablePlan;
import org.matsim.core.mobsim.qsim.agents.PersonDriverAgentImpl;
import org.matsim.core.mobsim.qsim.interfaces.MobsimVehicle;
import org.matsim.core.mobsim.qsim.interfaces.Netsim;
import org.matsim.facilities.Facility;
import org.matsim.vehicles.Vehicle;

/**
 * Delegate class that use SmartDriverLogic for route logic and
 * PersonDriverAgentImpl for other logic (e.g activity during day).
 * 
 * @see PersonDriverAgentImpl
 * @author Filippo Muzzini
 *
 */
/**
 * @author Filippo Muzzini
 *
 */
public class SmartPersonDriverAgentImpl implements MobsimDriverAgent, MobsimPassengerAgent, HasPerson, PlanAgent, HasModifiablePlan {
	
	private SmartDriverLogic smartDriverLogic;
	private PersonDriverAgentImpl personDriverAgent;

	/**
	 * Construct the agent with the specified logic.
	 * 
	 * @param plan1 Plan for PersonDriverAgentImpl
	 * @param simulation Simulation for PersonDriverAgentImpl
	 * @param smartDriverLogic the agent's logic object
	 */
	public SmartPersonDriverAgentImpl(Plan plan1, Netsim simulation, SmartDriverLogic smartDriverLogic) {
		personDriverAgent = new PersonDriverAgentImpl(plan1, simulation);
		if (smartDriverLogic == null) {
			smartDriverLogic = new StaticDriverLogic();
		}
		
		this.smartDriverLogic = smartDriverLogic;
		smartDriverLogic.setPersonDriverAgent(personDriverAgent);
	}
	
	public SmartPersonDriverAgentImpl(Plan plan1, Netsim simulation) {
		this(plan1, simulation, null);
	}
	
	@Override
	public void endLegAndComputeNextState(double now) {
		personDriverAgent.endLegAndComputeNextState(now);
	}

	@Override
	public void setStateToAbort(double now) {
		personDriverAgent.setStateToAbort(now);
	}

	@Override
	public void notifyArrivalOnLinkByNonNetworkMode(Id<Link> linkId) {
		personDriverAgent.notifyArrivalOnLinkByNonNetworkMode(linkId);
	}

	@Override
	public void endActivityAndComputeNextState(double now) {
		personDriverAgent.endActivityAndComputeNextState(now);
	}

	@Override
	public Id<Vehicle> getPlannedVehicleId() {
		return personDriverAgent.getPlannedVehicleId();
	}

	@Override
	public String getMode() {
		return personDriverAgent.getMode();
	}

	@Override
	public Double getExpectedTravelTime() {
		return personDriverAgent.getExpectedTravelTime();
	}

    @Override
    public Double getExpectedTravelDistance() {
        return personDriverAgent.getExpectedTravelDistance();
    }

    @Override
	public String toString() {
		return personDriverAgent.toString();
	}

	@Override
	public PlanElement getCurrentPlanElement() {
		return personDriverAgent.getCurrentPlanElement();
	}

	@Override
	public PlanElement getNextPlanElement() {
		return personDriverAgent.getNextPlanElement();
	}

	@Override
	public Plan getCurrentPlan() {
		return personDriverAgent.getCurrentPlan();
	}

	@Override
	public Id<Person> getId() {
		return personDriverAgent.getId();
	}

	@Override
	public  Person getPerson() {
		return personDriverAgent.getPerson();
	}

	@Override
	public MobsimVehicle getVehicle() {
		return personDriverAgent.getVehicle();
	}

	@Override
	public void setVehicle(MobsimVehicle vehicle) {
		personDriverAgent.setVehicle(vehicle);
	}

	@Override
	public Id<Link> getCurrentLinkId() {
		return personDriverAgent.getCurrentLinkId();
	}

	@Override
	public Id<Link> getDestinationLinkId() {
		return personDriverAgent.getDestinationLinkId();
	}

	@Override
	public double getActivityEndTime() {
		return personDriverAgent.getActivityEndTime();
	}

	@Override
	public State getState() {
		return personDriverAgent.getState();
	}

	@Override
	public void notifyMoveOverNode(Id<Link> newLinkId) {
		smartDriverLogic.notifyMoveOverNode(newLinkId);
	}

	@Override
	public Id<Link> chooseNextLinkId() {
		return smartDriverLogic.chooseNextLinkId();
	}

	@Override
	public boolean isWantingToArriveOnCurrentLink() {
		return smartDriverLogic.isWantingToArriveOnCurrentLink();
	}
	
	@Override
	public int getCurrentLinkIndex() {
		return personDriverAgent.getCurrentLinkIndex() ;
	}

	@Override
	public Plan getModifiablePlan() {
		return personDriverAgent.getModifiablePlan() ;
	}

	@Override
	public void resetCaches() {
		personDriverAgent.resetCaches();
		smartDriverLogic.resetCaches(); 
	}

	@Override
	public Facility<? extends Facility<?>> getCurrentFacility() {
		return this.personDriverAgent.getCurrentFacility();
	}

	@Override
	public Facility<? extends Facility<?>> getDestinationFacility() {
		return this.personDriverAgent.getDestinationFacility();
	}

	@Override
	public  PlanElement getPreviousPlanElement() {
		return this.personDriverAgent.getPreviousPlanElement();
	}


}
