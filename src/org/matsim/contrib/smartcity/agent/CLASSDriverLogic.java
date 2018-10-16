/**
 * 
 */
package org.matsim.contrib.smartcity.agent;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.contrib.smartcity.agent.routing.KShortestPath;
import org.matsim.contrib.smartcity.comunication.ComunicationClient;
import org.matsim.contrib.smartcity.comunication.ComunicationMessage;
import org.matsim.contrib.smartcity.comunication.ComunicationServer;
import org.matsim.contrib.smartcity.comunication.FlowRequest;
import org.matsim.contrib.smartcity.comunication.TrafficFlowMessage;
import org.matsim.contrib.smartcity.comunication.wrapper.ComunicationWrapper;
import org.matsim.contrib.smartcity.perception.TrafficFlow;
import org.matsim.core.gbl.MatsimRandom;
import org.matsim.core.network.algorithms.NetworkInverter;
import org.matsim.core.network.algorithms.NetworkTurnInfoBuilderI;
import org.matsim.core.utils.collections.Tuple;

import com.google.inject.Inject;

/**
 * @author Filippo Muzzini
 *
 */
public class CLASSDriverLogic extends AbstractDriverLogic implements AutonomousSpeed, ComunicationClient {

	private static final int ALTERNATIVE = 5;
	private TrafficFlow flow;
	private KShortestPath router;
	private Id<Link> nextLink;
	private boolean recomputeNextLink = true;
	private ComunicationWrapper wrapper;
	private Network network;
	
	@Inject
	public CLASSDriverLogic(Network network, NetworkTurnInfoBuilderI turn, ComunicationWrapper wrapper) {
		super();
		this.wrapper = wrapper;
		this.network = network;
		NetworkInverter inverter = NetworkInverterProvider.getInverted(network, turn);
		this.router = new KShortestPath(inverter, new TrafficFlow(), ALTERNATIVE);
	}

	/* (non-Javadoc)
	 * @see org.matsim.contrib.smartcity.agent.AutonomousSpeed#getSpeed()
	 */
	@Override
	public double getSpeed() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.matsim.contrib.smartcity.agent.AbstractDriverLogic#getNextLinkId()
	 */
	@Override
	public Id<Link> getNextLinkId() {
		if (recomputeNextLink == false) {
			return this.nextLink;
		}
		if (this.actualLink == this.getDestinationLinkId()) {
			return null;
		}
		
		HashMap<Id<Link>, Double> links = this.router.getNextLinks(this.actualLink, network);
		Double sumOfFlow = links.values().stream().mapToDouble(Double::doubleValue).sum();
		SortedMap<Double, Id<Link>> rouletteProb = new TreeMap<Double, Id<Link>>();
		double cumProb = 0.0;
		for (Entry<Id<Link>, Double> e : links.entrySet()) {
			Id<Link> link = e.getKey();
			double prob = 1 - (e.getValue()/sumOfFlow);
			cumProb += prob;
			rouletteProb.put(cumProb, link);
		}
		
		double r = MatsimRandom.getRandom().nextDouble();
		Double prob;
		Iterator<Double> iter = rouletteProb.keySet().iterator();
		do {
			prob = iter.next();	
		} while (r > prob && iter.hasNext());
		
		this.nextLink = rouletteProb.get(prob);
		this.recomputeNextLink = false;
		return rouletteProb.get(prob);
	}
	
	@Override
	public void setActualLink(Id<Link> linkId) {
		super.setActualLink(linkId);
		this.recomputeNextLink = true;
		
	}
	
	@Override
	public void setLeg(Leg leg) {
		super.setLeg(leg);
		Id<Node> startNode = Id.createNodeId(startLink);
		Id<Node> endNode = Id.createNodeId(endLink);
		Set<ComunicationServer> servers = this.discover();
		if (servers.size() > 0) {
			ComunicationServer server = servers.iterator().next();
			server.sendToMe(new FlowRequest(this));
		}
		this.router.setSource(startNode);
		this.router.setDest(endNode);
		this.setActualLink(startLink);
		this.router.route();
	}

	/* (non-Javadoc)
	 * @see org.matsim.contrib.smartcity.comunication.ComunicationClient#discover()
	 */
	@Override
	public Set<ComunicationServer> discover() {
		return this.wrapper.discover(this.actualLink);
	}

	/* (non-Javadoc)
	 * @see org.matsim.contrib.smartcity.comunication.ComunicationClient#sendToMe(org.matsim.contrib.smartcity.comunication.ComunicationMessage)
	 */
	@Override
	public void sendToMe(ComunicationMessage message) {
		if (message instanceof TrafficFlowMessage) {
			this.flow = ((TrafficFlowMessage) message).getFlow();
			this.router.flowChanged(flow);
			this.router.setSource(Id.createNodeId(actualLink));
			this.router.route();
		}
		
	}
	
	private static class NetworkInverterProvider {
		private final static HashMap<Tuple<Network, NetworkTurnInfoBuilderI>, NetworkInverter> net = new HashMap<Tuple<Network, NetworkTurnInfoBuilderI>, NetworkInverter>();
		
		public static NetworkInverter getInverted(Network network, NetworkTurnInfoBuilderI turn) {
			Tuple<Network, NetworkTurnInfoBuilderI> key = new Tuple<Network, NetworkTurnInfoBuilderI>(network, turn);
			NetworkInverter inverter = net.get(key);
			if (inverter == null) {
				inverter = new NetworkInverter(network, turn.createAllowedTurnInfos());
				net.put(key, inverter);
			}
			
			return inverter;
		}
	}

}
