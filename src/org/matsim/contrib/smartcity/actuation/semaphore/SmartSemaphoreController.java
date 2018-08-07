package org.matsim.contrib.smartcity.actuation.semaphore;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.contrib.signals.model.Signal;
import org.matsim.contrib.signals.model.SignalController;
import org.matsim.contrib.signals.model.SignalGroup;
import org.matsim.contrib.signals.model.SignalPlan;
import org.matsim.contrib.signals.model.SignalSystem;

/**
 * A simple logic for SmartSemaphore without communication
 * This class can be extended to implement more complex logics
 * and communications
 * 
 * @author Filippo Muzzini
 *
 */
public class SmartSemaphoreController implements SignalController {
	
	public static final String IDENTIFIER = "SmartSemphoreController";
	
	private double minimunGreenTime = 20;

	private SignalSystem system;
	private Id<SignalGroup> actualGreen;
	private double lastChange;
	private PriorityQueue<SemaphoreTime> queue;
	private List<Id<Link>> controlledLink;

	@Override
	public void updateState(double timeSeconds) {
		if (timeSeconds - this.lastChange < this.minimunGreenTime)
			return;
		
		this.nextGreen(timeSeconds);
	}
	
	private void nextGreen(double timeSeconds) {
		//the nextgreen is polled in the prority queue
		SemaphoreTime sTime = this.queue.poll();
		Id<SignalGroup> nextGreen = sTime.getSignalGroup();
		
		this.system.scheduleOnset(timeSeconds, nextGreen);
		if (this.actualGreen != null)
			this.system.scheduleDropping(timeSeconds, this.actualGreen);
		
		this.actualGreen = nextGreen;
		this.lastChange = timeSeconds;
		
		sTime.setLastTime(timeSeconds);
		this.queue.add(sTime);
	}

	@Override
	public void addPlan(SignalPlan plan) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reset(Integer iterationNumber) {
		// TODO Auto-generated method stub

	}

	@Override
	public void simulationInitialized(double simStartTimeSeconds) {
		this.nextGreen(simStartTimeSeconds);		
	}
	
	public List<Id<Link>> getControlledLink() {
		return this.controlledLink;
	}

	@Override
	public void setSignalSystem(SignalSystem signalSystem) {
		this.system = signalSystem;
		this.initPriorityQueue();
		this.initControlledLink();
	}
	
	private void initControlledLink() {
		this.controlledLink = new ArrayList<Id<Link>>();
		for (SignalGroup group : this.system.getSignalGroups().values()) {
			for (Signal signal :group.getSignals().values()) {
				Id<Link> linkId = signal.getLinkId();
				this.controlledLink.add(linkId);
			}
		}
	}

	private void initPriorityQueue() {
		int nGroup = this.system.getSignalGroups().size();
		this.queue = new PriorityQueue<SemaphoreTime>(nGroup, new SemaphoreTimeComparator());
		for (Id<SignalGroup> group : this.system.getSignalGroups().keySet()) {
			SemaphoreTime sTime = new SemaphoreTime(group);
			this.queue.add(sTime);
		}
	}
	
	private class SemaphoreTime {
		
		private double lastTime;
		private Id<SignalGroup> group;

		public SemaphoreTime(Id<SignalGroup> group) {
			this.group = group;
			this.lastTime = Double.NEGATIVE_INFINITY;
		}
		
		public Id<SignalGroup> getSignalGroup() {
			return this.group;
		}
		
		public void setLastTime(double time) {
			this.lastTime = time;
		}
		
	}
	
	private class SemaphoreTimeComparator implements Comparator<SemaphoreTime> {

		@Override
		public int compare(SemaphoreTime arg0, SemaphoreTime arg1) {
			double n0 = arg0.lastTime;
			double n1 = arg1.lastTime;
			
			return Double.compare(n0, n1);
		}
		
	}

}
