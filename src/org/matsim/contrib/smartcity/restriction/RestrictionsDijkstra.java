/**
 * 
 */
package org.matsim.contrib.smartcity.restriction;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;
import org.matsim.core.router.Dijkstra;
import org.matsim.core.router.util.TravelDisutility;
import org.matsim.core.router.util.TravelTime;
import org.matsim.core.utils.collections.RouterPriorityQueue;

/**
 * LeastCostPathCalculator that is restrictions aware.
 * This class use Dijkstra algorithm
 * @author Filippo Muzzini
 *
 */
public class RestrictionsDijkstra extends Dijkstra {
	
	public static final String RESTRICTION_ATT = "restriction";
	public static final String RESTRICTION_SEP = ";";

	/**
	 * @param network
	 * @param costFunction
	 * @param timeFunction
	 */
	protected RestrictionsDijkstra(Network network, TravelDisutility costFunction, TravelTime timeFunction) {
		super(network, costFunction, timeFunction);
	}

	@Override
	protected boolean addToPendingNodes(final Link l, final Node n,
			final RouterPriorityQueue<Node> pendingNodes, final double currTime,
			final double currCost, final Node toNode) {
		Link fromLink = getData(n).getPrevLink();
		
		if (fromLink != null && getInibithion(fromLink).contains(l.getId())){
			return false;
		} else {
			return super.addToPendingNodes(l, n, pendingNodes, currTime, currCost, toNode);
		}
		
	}
	
	/**
	 * @param fromLink
	 * @return
	 */
	private Set<Id<Link>> getInibithion(Link fromLink) {
		String inibStr = (String) fromLink.getAttributes().getAttribute(RESTRICTION_ATT);
		if (inibStr == null) {
			return new HashSet<Id<Link>>();
		}
		List<String> inibList = Arrays.asList(inibStr.split(RESTRICTION_SEP));
		return inibList.stream().map(Id::createLinkId).collect(Collectors.toSet());
		
	}
}
