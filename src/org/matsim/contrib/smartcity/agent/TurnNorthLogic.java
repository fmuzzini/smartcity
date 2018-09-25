/**
 * 
 */
package org.matsim.contrib.smartcity.agent;

import java.util.Map;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;

import com.google.inject.Inject;

/**
 * @author Filippo Muzzini
 *
 */
public class TurnNorthLogic extends AbstractDriverLogic {

	@Inject private Network network;
	private Id<Link> nextLinkId;
	
	@Override
	public Id<Link> getNextLinkId() {
		doRight();
		return nextLinkId;
	}

	/**
	 * 
	 */
	private void doRight() {
		Id<Link> currentLinkId = this.actualLink;
		// Where do I want to move next?
				Link currentLink = network.getLinks().get(currentLinkId);
				//Node fromNode = currentLink.getFromNode();
				//Node toNode = currentLink.getToNode();
				Map<Id<Link>, ? extends Link> possibleNextLinks = currentLink.getToNode().getOutLinks();
				
				double maxNord = currentLink.getToNode().getCoord().getY();
				Link maxLink = null;
				for (Link link : possibleNextLinks.values()) {
					double nord = link.getToNode().getCoord().getY();
					if (nord >= maxNord) {
						maxLink = link;
						maxNord = nord;
					}
				}
				

				if (maxLink != null) {
					nextLinkId = maxLink.getId();
				} else {
					nextLinkId = possibleNextLinks.values().iterator().next().getId();
				}
		
	}

}
