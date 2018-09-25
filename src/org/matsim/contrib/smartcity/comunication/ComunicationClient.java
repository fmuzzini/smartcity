/**
 * 
 */
package org.matsim.contrib.smartcity.comunication;

import java.util.Set;

/**
 * Interface for client of cumunications system
 * @author Filippo Muzzini
 *
 */
public interface ComunicationClient {
	
	/**
	 * Return the set of reachable servers
	 * @return
	 */
	public Set<ComunicationServer> discover();
	
	/**
	 * Called by other communications entities for send a message to this agent
	 * @param message
	 */
	public void sendToMe(ComunicationMessage message);

}
