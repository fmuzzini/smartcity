/**
 * 
 */
package org.matsim.contrib.smartcity.agent.routing;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;
import org.matsim.contrib.smartcity.perception.TrafficFlow;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.algorithms.NetworkInverter;

/**
 * @author Filippo Muzzini
 *
 */
public class KShortestPath {

	private Network network;
	private TrafficFlow flow;
	private int k;
	private List<List<Id<Link>>> paths;
	private Id<Node> dest;
	private Id<Node> source;
	private NetworkInverter inverter;

	public KShortestPath(NetworkInverter inverter, TrafficFlow flow, int k) {
		this.network = inverter.getInvertedNetwork();
		this.inverter = inverter;
		this.flow = flow;
		this.k = k;
		this.paths = new ArrayList<List<Id<Link>>>();
	}

	/**
	 * 
	 */
	public void route() {
		PriorityQueue<Path> P = new PriorityQueue<Path>(new Path());
		PriorityQueue<Path> B = new PriorityQueue<Path>(new Path());
		HashMap<Id<Node>, Integer> count = new HashMap<Id<Node>, Integer>();
		count.put(dest, 0);
		ArrayList<Id<Node>> ps = new ArrayList<Id<Node>>();
		ps.add(source);
		B.add(new Path(ps, 0.0));
		while (!B.isEmpty() && count.get(dest) < k) {
			Path pu = B.poll();
			Id<Node> u = pu.path.get(pu.path.size()-1);
			Integer countU = count.get(u);
			if (countU == null) {
				countU = 1;
			} else {
				countU++;
			}
			count.put(u, countU);
			if (u.equals(this.dest)) {
				P.add(pu);
			}
			if (countU <= k) {
				for (Id<Node> v : adjacent(u)) {
					if (!pu.contains(v)) {
						ArrayList<Id<Node>> p = new ArrayList<Id<Node>>(pu.path);
						Node nodeU = network.getNodes().get(u);
						Node nodeV = network.getNodes().get(v);
						p.add(v);
						Double cost = pu.cost + calcCost(NetworkUtils.getConnectingLink(nodeU, nodeV));
						Path pv = new Path(p, cost);
						B.add(pv);
					}
				}
			}
		}
		
		for (Path p : P) {
			List<Node> invPath = p.path.stream().map(n -> this.network.getNodes().get(n)).collect(Collectors.toList());
			List<Id<Link>> path = inverter.convertInvertedNodesToLinks(invPath).stream().
					map(l -> l.getId()).collect(Collectors.toList());
			this.paths.add(path);			
		}
		
	}
	
	/**
	 * @param connectingLink
	 * @return
	 */
	private Double calcCost(Link link) {
		double cost = link.getLength() / link.getFreespeed();
		if (this.flow.getFlow(link.getId()) != null) {
			cost = cost * this.flow.getFlow(link.getId());
		}
		
		return cost;
	}

	/**
	 * @param u
	 * @return
	 */
	private List<Id<Node>> adjacent(Id<Node> u) {
		Node node = network.getNodes().get(u);
		return node.getOutLinks().values().stream().map(l -> l.getToNode().getId()).collect(Collectors.toList());
	}

	public void setDest(Id<Node> dest) {
		this.dest = dest;
	}
	
	public void setSource(Id<Node> source) {
		this.source = source;
	}
	
	public HashMap<Id<Link>, Double> getNextLinks(Id<Link> actualLink, Network net){
		HashMap<Id<Link>, Double> nextLinks = new HashMap<Id<Link>, Double>();
		for (List<Id<Link>> path : paths) {
			int n = path.indexOf(actualLink);
			if (n == path.size()-1 || n == -1) {
				continue;
			}
			Id<Link> nextLink = path.get(n+1);
			Double nextFlow = calcCostToEnd(path, nextLink, net);
			if (nextFlow == null) {
				nextFlow = 0.0;
			}
			nextLinks.put(nextLink, nextFlow);
		}
		
		return nextLinks;
	}
	
	/**
	 * @param nextLink
	 * @param net
	 * @return
	 */
	private Double calcCostToEnd(List<Id<Link>> path, Id<Link> nextLink, Network net) {
		Double res = 0.0;
		int n = path.indexOf(nextLink);
		for (int i=n; i<path.size(); i++) {
			Link link = net.getLinks().get(path.get(i));
			res += calcCost(link);
		}
		
		return res;
	}

	public void flowChanged(TrafficFlow flow) {
		this.flow = flow;
	}
	
	private class Path implements Comparator<Path> {
		List<Id<Node>> path;
		Double cost;
		
		public Path() {
			
		}
		
		/**
		 * @param v
		 * @return
		 */
		public boolean contains(Id<Node> v) {
			return path.contains(v);
		}

		public Path(List<Id<Node>> path, Double cost) {
			this.path = path;
			this.cost = cost;
		}

		/* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(Path arg0, Path arg1) {
			return Double.compare(arg0.cost, arg1.cost);
		}
	}
	
}
