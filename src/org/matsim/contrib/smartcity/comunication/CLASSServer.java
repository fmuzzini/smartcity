/**
 * 
 */
package org.matsim.contrib.smartcity.comunication;

import java.util.Collection;
import java.util.HashSet;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.contrib.smartcity.comunication.wrapper.ComunicationFixedWrapper;
import org.matsim.contrib.smartcity.perception.TrafficFlow;
import org.matsim.contrib.smartcity.perception.camera.ActiveCamera;
import org.matsim.contrib.smartcity.perception.camera.CameraListener;
import org.matsim.contrib.smartcity.perception.camera.CameraStatus;

import com.google.inject.Inject;

/**
 * @author Filippo Muzzini
 *
 */
public class CLASSServer implements ComunicationServer, CameraListener {

	private static final double CRITICAL_FLOW = 0.75;
	
	private ComunicationFixedWrapper wrapper;
	@Inject Network network;
	private TrafficFlow flow = new TrafficFlow();
	
	public CLASSServer(ComunicationFixedWrapper wrapper, HashSet<Coord> positions, Collection<ActiveCamera> cameras) {
		this.wrapper = wrapper;
		this.wrapper.addFixedComunicator(this, positions);
		for (ActiveCamera camera : cameras) {
			camera.addCameraListener(this);
		}
	}

	/* (non-Javadoc)
	 * @see org.matsim.contrib.smartcity.comunication.ComunicationServer#sendToMe(org.matsim.contrib.smartcity.comunication.ComunicationMessage)
	 */
	@Override
	public void sendToMe(ComunicationMessage message) {
		if (message instanceof FlowRequest) {
			message.getSender().sendToMe(createFlowMessage());
		}
	}

	/**
	 * @return
	 */
	private ComunicationMessage createFlowMessage() {
		return new TrafficFlowMessage(this, flow);
	}

	/* (non-Javadoc)
	 * @see org.matsim.contrib.smartcity.perception.camera.CameraListener#pushCameraStatus(org.matsim.contrib.smartcity.perception.camera.CameraStatus)
	 */
	@Override
	public void pushCameraStatus(CameraStatus status) {
		Id<Link> link = status.getIdLink();
		int vehs = status.getLinkStatus().getTotal();
		Double flow = new Double(vehs);
		this.flow.addFlow(link, flow);
		
		if (flow >= getCriticalFlow(link)) {
			ComunicationMessage message = createFlowMessage();
			wrapper.broadcast(message);
		}
	}

	/**
	 * @param link
	 * @return
	 */
	private Double getCriticalFlow(Id<Link> link) {
		double instantCap = network.getLinks().get(link).getCapacity() / network.getCapacityPeriod();
		return instantCap * CRITICAL_FLOW;		
	}

}
