/**
 * 
 */
package org.matsim.contrib.smartcity.agent;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.core.mobsim.qsim.agents.PersonDriverAgentImpl;

/**
 * Abstract class that implements a standard behavior of an agent logic.
 * This simplify the implementations of SmartDriverLogic interface.
 * 
 * The only abstract method is chooseNextLinkId; implement in this method the
 * smart behavior of agent.
 * 
 * Furthermore is possible overriding all methods of SmartDriverLogic interface. 
 * 
 * @author Filippo Muzzini
 *
 */
public abstract class AbstractDriverLogic implements SmartDriverLogic {

	protected PersonDriverAgentImpl personDriverAgent;

	/* (non-Javadoc)
	 * @see org.matsim.core.mobsim.framework.DriverAgent#chooseNextLinkId()
	 */
	@Override
	public abstract Id<Link> chooseNextLinkId();

	/* (non-Javadoc)
	 * @see org.matsim.core.mobsim.framework.DriverAgent#notifyMoveOverNode(org.matsim.api.core.v01.Id)
	 */
	@Override
	public void notifyMoveOverNode(Id<Link> newLinkId) {
		this.personDriverAgent.notifyMoveOverNode(newLinkId);
	}

	/* (non-Javadoc)
	 * @see org.matsim.core.mobsim.framework.DriverAgent#isWantingToArriveOnCurrentLink()
	 */
	@Override
	public boolean isWantingToArriveOnCurrentLink() {
		return this.personDriverAgent.isWantingToArriveOnCurrentLink();
	}

	/* (non-Javadoc)
	 * @see org.matsim.contrib.smartcity.agent.SmartDriverAgent#setPersonDriverAgent(org.matsim.core.mobsim.qsim.agents.PersonDriverAgentImpl)
	 */
	@Override
	public void setPersonDriverAgent(PersonDriverAgentImpl personDriverAgent) {
		this.personDriverAgent = personDriverAgent;
	}

	/* (non-Javadoc)
	 * @see org.matsim.contrib.smartcity.agent.SmartDriverAgent#resetCaches()
	 */
	@Override
	public void resetCaches() {
		this.personDriverAgent.resetCaches();
	}

}
