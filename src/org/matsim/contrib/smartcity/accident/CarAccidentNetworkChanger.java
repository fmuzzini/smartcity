/**
 * 
 */
package org.matsim.contrib.smartcity.accident;

import java.util.ArrayList;
import java.util.List;

import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.mobsim.framework.events.MobsimBeforeCleanupEvent;
import org.matsim.core.mobsim.framework.events.MobsimBeforeSimStepEvent;
import org.matsim.core.mobsim.framework.listeners.MobsimBeforeCleanupListener;
import org.matsim.core.mobsim.framework.listeners.MobsimBeforeSimStepListener;
import com.google.inject.Inject;

/**
 * Class that reduce the link capacity after an accident.
 * Thought the day time the capacity is modelled.
 * @author Filippo Muzzini
 *
 */
public class CarAccidentNetworkChanger implements AccidentEventHandler, MobsimBeforeSimStepListener, MobsimBeforeCleanupListener {
	
	private static final double LIMITING_TIME = 1500;

	@Inject private Network network;
	
	private ArrayList<LimitedLink> limitedLinks = new ArrayList<LimitedLink>();
	
	@Inject
	public CarAccidentNetworkChanger(EventsManager events) {
		events.addHandler(this);
	}

	/* (non-Javadoc)
	 * @see org.matsim.contrib.smartcity.accident.AccidentEventHandler#handleEvent(org.matsim.contrib.smartcity.accident.CarAccidentEvent)
	 */
	@Override
	public void handleEvent(CarAccidentEvent e) {
		int involvedCars = e.getOthers() != null ? e.getOthers().size() + 2 : 2 ;
		double startTime = e.getTime();
		List<Link> involvedLinks = new ArrayList<Link>();
		involvedLinks.add(network.getLinks().get(e.getFromId()));
		involvedLinks.add(network.getLinks().get(e.getToId()));
		
		for (Link link : involvedLinks) {
			LimitedLink limitedLink = new LimitedLink(link, startTime, involvedCars);
			this.limitedLinks.add(limitedLink);
		}
	}
	
	private void doSimStep(double now) {
		for (LimitedLink limited : limitedLinks) {
			Link link = limited.getLink();
			double startTime = limited.getStartTime();
			int involvedCars = limited.getInvolvedCars();
			
			double capacityRatio = calcFlow(startTime, now, involvedCars);
			if (capacityRatio == 1) {
				limitedLinks.remove(limited);
				return;
			}
			double newCapacity = limited.getStartCapacity() * capacityRatio;
			link.setCapacity(newCapacity);
		}
	}
	
	/**
	 * @param startTime
	 * @param now
	 * @param involvedCars
	 * @return
	 */
	private double calcFlow(double startTime, double now, int involvedCars) {
		double offset = now - startTime;
		return twoStepFunction(offset);
	}

	/**
	 * @param offset
	 * @return
	 */
	private double twoStepFunction(double x) {
		double x1 = 0.25 * LIMITING_TIME;
		double x2 = 0.75 * LIMITING_TIME;
		if (x < x1)
			return 0;
		if (x > x2)
			return 1;
		return 0.5;
	}

	/**
	 * Class that memorize the sate of a link
	 * @author Filippo Muzzini
	 *
	 */
	private class LimitedLink {
		
		private Link link;
		private double startTime;
		private int involvedCars;
		private double startCapacity;
		
		/**
		 * @param link
		 * @param startTime
		 * @param involvedCars
		 */
		public LimitedLink(Link link, double startTime, int involvedCars) {
			this.link = link;
			this.startTime = startTime;
			this.involvedCars = involvedCars;
			
			this.startCapacity = link.getCapacity();
		}

		public Link getLink() {
			return link;
		}

		public double getStartTime() {
			return startTime;
		}

		public int getInvolvedCars() {
			return involvedCars;
		}
		
		public double getStartCapacity() {
			return startCapacity;
		}
				
	}

	/* (non-Javadoc)
	 * @see org.matsim.core.mobsim.framework.listeners.MobsimBeforeSimStepListener#notifyMobsimBeforeSimStep(org.matsim.core.mobsim.framework.events.MobsimBeforeSimStepEvent)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public void notifyMobsimBeforeSimStep(MobsimBeforeSimStepEvent e) {
		double now = e.getSimulationTime();
		doSimStep(now);
	}

	/* (non-Javadoc)
	 * @see org.matsim.core.mobsim.framework.listeners.MobsimBeforeCleanupListener#notifyMobsimBeforeCleanup(org.matsim.core.mobsim.framework.events.MobsimBeforeCleanupEvent)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public void notifyMobsimBeforeCleanup(MobsimBeforeCleanupEvent e) {
		limitedLinks.clear();
	}

}
