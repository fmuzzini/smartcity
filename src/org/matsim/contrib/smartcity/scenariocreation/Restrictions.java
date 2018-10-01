/**
 * 
 */
package org.matsim.contrib.smartcity.scenariocreation;

import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.alex73.osmemory.IOsmNode;
import org.alex73.osmemory.IOsmObject;
import org.alex73.osmemory.IOsmRelation;
import org.alex73.osmemory.IOsmWay;
import org.alex73.osmemory.MemoryStorage;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;
import org.matsim.contrib.smartcity.restriction.NetworkWithRestrictionTurnInfoBuilder;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.NetworkWriter;

/**
 * Container that extract the restriction from osm storage
 * @author Filippo Muzzini
 *
 */
public class Restrictions {

	private static final String RESTRICTION_TAG = "restriction";
	private static final String FROM_ROLE = "from";
	private static final String TO_ROLE = "to";
	private static final String VIA_ROLE = "via";
	private static final String DEFAULT_OUTPUT_DIR = ".";
	private static final String DEFAULT_OUTPUT_NAME = "NetworkWithRestriction";
	private MemoryStorage storage;
	private Network network;

	public Restrictions(MemoryStorage storage, Network network) {
		this.storage = storage;
		this.network = network;
		process();
	}

	/**
	 * 
	 */
	private void process() {
		storage.byTag(RESTRICTION_TAG, o -> o.isRelation(), o -> processRestriction(o));
		
	}

	/**
	 * @param o
	 * @return
	 */
	private void processRestriction(IOsmObject o) {
		IOsmRelation r = (IOsmRelation) o;
		int n = r.getMembersCount();
		IOsmWay from = null;
		IOsmWay to = null;
		IOsmNode node = null;
		for (int i = 0; i<n; i++) {
			String memberRole = r.getMemberRole(storage, i);
			IOsmWay way = null;
			IOsmNode node_ = null;
			if (r.getMemberObject(storage, i).isWay()) {
				way = (IOsmWay) r.getMemberObject(storage, i);
			} else if (r.getMemberObject(storage, i).isNode()) {
				node_ = (IOsmNode) r.getMemberObject(storage, i);
			} else {
				continue;
			}
			if (memberRole.equals(FROM_ROLE)) {
				from = way;
			}
			
			if (memberRole.equals(TO_ROLE)){
				to = way;
			}
			
			if (memberRole.equals(VIA_ROLE)) {
				node = node_;
			}
		}
		
		Node matsimNode = network.getNodes().get(Id.createNodeId(node.getId()));
		Map<Id<Link>, ? extends Link> inLinks = matsimNode.getInLinks();
		Map<Id<Link>, ? extends Link> outLinks = matsimNode.getOutLinks();
		Set<Id<Link>> fromLinks = getWayLinks(from); 
		Set<Id<Link>> toLinks = getWayLinks(to);
		
		fromLinks.retainAll(inLinks.keySet());
		toLinks.retainAll(outLinks.keySet());
		
		Id<Link> fromLink = fromLinks.iterator().next();
		Id<Link> toLink = toLinks.iterator().next();
		setAttributeRestriction(fromLink, toLink);
		
	}

	/**
	 * @param fromLink
	 * @param toLink
	 */
	private void setAttributeRestriction(Id<Link> fromLink, Id<Link> toLink) {
		Link from = network.getLinks().get(fromLink);
		String restrs = (String) from.getAttributes().getAttribute(NetworkWithRestrictionTurnInfoBuilder.RESTRICTION_ATT);
		restrs = restrs != null ? restrs+NetworkWithRestrictionTurnInfoBuilder.RESTRICTION_SEP : "";
		restrs = restrs + toLink;
		from.getAttributes().putAttribute(NetworkWithRestrictionTurnInfoBuilder.RESTRICTION_ATT, restrs);
	}

	/**
	 * @param from
	 * @return
	 */
	private Set<Id<Link>> getWayLinks(IOsmWay wayId) {
		String wayIdStr = new Long(wayId.getId()).toString();
		return network.getLinks().entrySet().stream().filter(
				e -> e.getValue().getAttributes().getAttribute(NetworkUtils.ORIGID).equals(wayIdStr)).map(
						e -> e.getKey()).collect(Collectors.toSet());
	}

	/**
	 * @param outputFile 
	 * 
	 */
	public void write(String outputFile) {
		if (outputFile == null) {
			outputFile = DEFAULT_OUTPUT_DIR + File.separator + DEFAULT_OUTPUT_NAME;
		}
		NetworkWriter writer = new NetworkWriter(network);
		writer.write(outputFile);
		
	}

}
