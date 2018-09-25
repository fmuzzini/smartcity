/**
 * 
 */
package org.matsim.contrib.smartcity.comunication;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import org.matsim.api.core.v01.Coord;
import org.matsim.core.utils.collections.Tuple;
import org.matsim.core.utils.io.MatsimXmlParser;
import org.xml.sax.Attributes;

/**
 * Reader for server file
 * @author Filippo Muzzini
 *
 */
public class ComunicationServerListXMLReader extends MatsimXmlParser {
	
	public static final String SERVERTAG = "server";
	public static final String COORDTAG = "coord";
	private static final String XATT = "x";
	private static final String YATT = "y";
	private static final String IDATT = "id";
	private static final String CLASSATT = "class";
	
	private HashMap<Tuple<String, String>, Set<Coord>> servers = new HashMap<Tuple<String, String>, Set<Coord>>();
	private Set<Coord> actualSet;
	private Tuple<String, String> actualServer;
	
	public Set<Tuple<String, String>> getServerList() {
		return servers.keySet();
	}
	
	public Set<Coord> getServerCoord(Tuple<String, String> serverId) {
		return servers.get(serverId);
	}

	/* (non-Javadoc)
	 * @see org.matsim.core.utils.io.MatsimXmlParser#startTag(java.lang.String, org.xml.sax.Attributes, java.util.Stack)
	 */
	@Override
	public void startTag(String name, Attributes atts, Stack<String> context) {
		switch (name) {
			case SERVERTAG:
				actualSet = new HashSet<Coord>();
				String serverId = atts.getValue(IDATT);
				String serverClass = atts.getValue(CLASSATT);
				actualServer = new Tuple<String, String>(serverId, serverClass);
				break;
			case COORDTAG:
				double x = Double.parseDouble(atts.getValue(XATT));
				double y = Double.parseDouble(atts.getValue(YATT));
				Coord coord = new Coord(x,y);
				actualSet.add(coord);
				break;
		}
	}

	/* (non-Javadoc)
	 * @see org.matsim.core.utils.io.MatsimXmlParser#endTag(java.lang.String, java.lang.String, java.util.Stack)
	 */
	@Override
	public void endTag(String name, String content, Stack<String> context) {
		switch (name) {
		case SERVERTAG:
			servers.put(actualServer, actualSet);
			break;
		case COORDTAG:
			break;
	}

	}

}
