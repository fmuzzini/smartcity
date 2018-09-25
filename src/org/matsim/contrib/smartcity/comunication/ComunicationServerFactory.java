/**
 * 
 */
package org.matsim.contrib.smartcity.comunication;

import java.util.Set;

import org.matsim.api.core.v01.Coord;

/**
 * Factory for server creation
 * @author Filippo Muzzini
 *
 */
public interface ComunicationServerFactory {

	/**
	 * @param serverId 
	 * @param serverClass java class of server
	 * @param coord set of coords of position of server appendices
	 */
	void instantiateServer(String serverId, String serverClass, Set<Coord> coord);

}
