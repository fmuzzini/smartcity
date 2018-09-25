/**
 * 
 */
package org.matsim.contrib.smartcity.restriction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.vehicles.Vehicle;

/**
 * @author Filippo Muzzini
 *
 */
public class ProhibitionPathCalculator implements LeastCostPathCalculator {
	
	private static final String INIBITHION_ATT = null;
	private Network network;
	private HashMap<Id<Node>, Double> dist;
	private HashMap<Id<Node>, Id<Node>> prec;
	private HashSet<Id<Node>> Q;

	public ProhibitionPathCalculator(Network network) {
		this.network = network;
		dist = new HashMap<Id<Node>, Double>();
		prec = new HashMap<Id<Node>, Id<Node>>();
		Q = new HashSet<Id<Node>>());
	}

	/* (non-Javadoc)
	 * @see org.matsim.core.router.util.LeastCostPathCalculator#calcLeastCostPath(org.matsim.api.core.v01.network.Node, org.matsim.api.core.v01.network.Node, double, org.matsim.api.core.v01.population.Person, org.matsim.vehicles.Vehicle)
	 */
	@Override
	public Path calcLeastCostPath(Node fromNode, Node toNode, double starttime, Person person, Vehicle vehicle) {
		
		for (Node node : network.getNodes().values()) {
			updateNode(node.getId(), Double.POSITIVE_INFINITY, null);
			Q.add(node.getId());			
		}
		dist.put(fromNode.getId(), 0.0);
		
		while (!Q.isEmpty()) {
			Id<Node> u = getMinNode();
			Q.remove(u);
			if (u.equals(toNode.getId())) {
				break;
			}
			if (dist.get(u) == Double.POSITIVE_INFINITY) {
				break;
			}
			
			for (Id<Node> v : getNeighbour(u)) {
				Double alt = dist.get(u) + calcDist(u,v);
				if (alt < dist.get(v)) {
					updateNode(v, alt, u);
				}
			}
		}
		
		return new Path();
		
		
	}
	
	/**
	 * @param u
	 * @param v
	 * @return
	 */
	private Double calcDist(Id<Node> u, Id<Node> v) {
		Node fromNode = network.getNodes().get(prec.get(u));
		Node toNode = network.getNodes().get(u);
		Node vNode = network.getNodes().get(v);
		Link fromLink = NetworkUtils.getConnectingLink(fromNode, toNode);
		Link actualLink = NetworkUtils.getConnectingLink(toNode, vNode);
		
		if (getInibithion(fromLink).contains(actualLink.getId())){
			return Double.POSITIVE_INFINITY;
		} else {
			return actualLink.getLength();
		}
		
	}

	/**
	 * @param fromLink
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private HashSet<Id<Link>> getInibithion(Link fromLink) {
		return (HashSet<Id<Link>>) fromLink.getAttributes().getAttribute(INIBITHION_ATT);
	}

	/**
	 * @param u
	 * @return
	 */
	private Set<Id<Node>> getNeighbour(Id<Node> u) {
		Node node = network.getNodes().get(u);
		return node.getOutLinks().values().stream().map(l -> l.getToNode().getId()).collect(Collectors.toSet());
	}

	private void updateNode(Id<Node> v, Double dist, Id<Node> u) {
		this.dist.put(v, Double.POSITIVE_INFINITY);
		this.prec.put(v, u);
	}
	
	private Id<Node> getMinNode(){
		double min = Double.POSITIVE_INFINITY;
		Id<Node> minNode = null;
		for (Id<Node> node : dist.keySet()) {
			Double nodeDist = dist.get(node);
			if (nodeDist < min) {
				min = nodeDist;
				minNode = node;
			}
		}
		return minNode;
	}

}
