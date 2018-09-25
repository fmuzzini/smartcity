/**
 * 
 */
package org.matsim.contrib.smartcity.comunication;

/**
 * Interface for server comunication
 * @author Filippo Muzzini
 *
 */
public interface ComunicationServer {
	
	/**
	 * Called by entites that want senda a message
	 * @param message
	 */
	public void sendToMe(ComunicationMessage message);

}
