/**
 * 
 */
package org.matsim.contrib.smartcity.agent;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.core.mobsim.qsim.agents.PersonDriverAgentImpl;

/**
 * Interface that define the method that can be called for using an agent logic.
 * PersonDriverAgentImpl can be used to call a standard behavior of agents
 * in order to simplify the implementation of smart logic.
 * 
 * @see AbstractDriverLogic
 * @see PersonDriverAgentImpl
 * @author Filippo Muzzini
 *
 */
public interface SmartDriverLogic {

	/**Set the PersonDriverAgentImpl instance
	 * 
	 * @param personDriverAgent
	 */
	public void setPersonDriverAgent(PersonDriverAgentImpl personDriverAgent);
	
	/**
	 * For compatibility to PersonDriverAgentImpl method
	 */
	public void resetCaches();

	/**
	 * Return the next link that the agent must take.
	 * In this method there is the logic.
	 * 
	 * @return next link
	 */
	public Id<Link> chooseNextLinkId();

	/**
	 * Called when the agent was moved over node to new link.
	 * 
	 * @param newLinkId
	 */
	public void notifyMoveOverNode(Id<Link> newLinkId);

	/**
	 * Return if the agent want to stop on this link;
	 * e.g. the agent is arrived to destination.
	 * 
	 */
	public boolean isWantingToArriveOnCurrentLink();

}
