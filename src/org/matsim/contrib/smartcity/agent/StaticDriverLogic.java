/**
 * 
 */
package org.matsim.contrib.smartcity.agent;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;

/**
 * This class implements a static logic without smart behavior.
 * The agent with this logic take the planned road.
 * 
 * @author Filippo Muzzini
 *
 */
public class StaticDriverLogic extends AbstractDriverLogic {

	/* (non-Javadoc)
	 * @see org.matsim.contrib.smartcity.agent.AbstractDriverLogic#chooseNextLinkId()
	 */
	@Override
	public Id<Link> chooseNextLinkId() {
		return this.personDriverAgent.chooseNextLinkId();
	}

	

}
