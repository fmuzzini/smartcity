/**
 * 
 */
package org.matsim.contrib.smartcity.comunication;

import java.util.Set;

import org.matsim.api.core.v01.Coord;
import org.matsim.contrib.smartcity.InstantationUtils;

import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * Basic ServerFactory
 * @author Filippo Muzzini
 *
 */
public class ComunicationServerFactoryImpl implements ComunicationServerFactory {
	
	@Inject private Injector inj;

	/* (non-Javadoc)
	 * @see org.matsim.contrib.smartcity.comunication.ComunicationServerFactory#instantiateServer(java.lang.String, java.lang.String, java.util.Set)
	 */
	@Override
	public void instantiateServer(String serverId, String serverClass, Set<Coord> coord) {
		Object[] params = {serverId, coord};
		InstantationUtils.instantiateForNameWithParams(inj, serverClass, params);		
	}

}
