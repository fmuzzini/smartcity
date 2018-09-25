/**
 * 
 */
package org.matsim.contrib.smartcity.comunication;

/**
 * Message of comunication
 * @author Filippo Muzzini
 *
 */
public class ComunicationMessage {

	private ComunicationClient sender;
	
	public ComunicationMessage(ComunicationClient sender) {
		this.sender = sender;
	}


	public ComunicationClient getSender() {
		return this.sender;
	}

}
