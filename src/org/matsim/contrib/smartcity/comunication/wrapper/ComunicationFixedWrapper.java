/**
 * 
 */
package org.matsim.contrib.smartcity.comunication.wrapper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.contrib.smartcity.comunication.ComunicationConfigGroup;
import org.matsim.contrib.smartcity.comunication.ComunicationServer;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.network.NetworkUtils;

import com.google.inject.Inject;

/**
 * Wrapper for systems with fixed appendices
 * @author Filippo Muzzini
 *
 */
public class ComunicationFixedWrapper implements ComunicationWrapper {
	
	@Inject private Network network;
	
	private double range;
	private HashMap<Coord, Set<ComunicationServer>> fixed = new HashMap<Coord, Set<ComunicationServer>>();
	
	@Inject
	public ComunicationFixedWrapper(Config config) {
		ComunicationConfigGroup configGroup = ConfigUtils.addOrGetModule(config, ComunicationConfigGroup.class);
		this.range = Double.parseDouble(configGroup.getParams().get(ComunicationConfigGroup.RANGE));
	}

	/* (non-Javadoc)
	 * @see org.matsim.contrib.smartcity.comunication.ComunicationWrapper#discover(org.matsim.api.core.v01.Id)
	 */
	@Override
	public Set<ComunicationServer> discover(Id<Link> position) {
		Link actualLink = network.getLinks().get(position);
		return fixed.entrySet().stream().filter(
				c -> NetworkUtils.getEuclideanDistance(actualLink.getCoord(), c.getKey()) <= range 
				).map(e -> e.getValue()).flatMap(s -> s.stream()).collect(Collectors.toSet());
		
	}
	
	/**
	 * Add server with his appendices
	 * @param server Server
	 * @param positions set of appendices' position
	 */
	public void addFixedComunicator(ComunicationServer server, Set<Coord> positions) {
		for (Coord c : positions) {
			Set<ComunicationServer> serverSet = fixed.get(c);
			if (serverSet == null)
				serverSet = new HashSet<ComunicationServer>();
			serverSet.add(server);
			fixed.put(c, serverSet);
		}
	}

}
