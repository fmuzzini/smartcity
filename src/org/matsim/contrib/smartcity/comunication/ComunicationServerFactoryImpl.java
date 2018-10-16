/**
 * 
 */
package org.matsim.contrib.smartcity.comunication;

import java.util.Set;
import java.util.stream.Collectors;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.contrib.smartcity.InstantationUtils;
import org.matsim.contrib.smartcity.perception.CamerasContainer;
import org.matsim.contrib.smartcity.perception.camera.Camera;

import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * Basic ServerFactory
 * @author Filippo Muzzini
 *
 */
public class ComunicationServerFactoryImpl implements ComunicationServerFactory {
	
	@Inject private Injector inj;
	@Inject private CamerasContainer camCont;

	/* (non-Javadoc)
	 * @see org.matsim.contrib.smartcity.comunication.ComunicationServerFactory#instantiateServer(java.lang.String, java.lang.String, java.util.Set)
	 */
	@Override
	public void instantiateServer(String serverId, String serverClass, Set<Coord> coord,
			Set<Id<Camera>> camerasId) {
		Set<Camera> cameras = camerasId.stream().map(id -> camCont.getCamera(id)).collect(Collectors.toSet());
		Object[] params = {serverId, coord, cameras};
		InstantationUtils.instantiateForNameWithParams(inj, serverClass, params);		
	}

}
