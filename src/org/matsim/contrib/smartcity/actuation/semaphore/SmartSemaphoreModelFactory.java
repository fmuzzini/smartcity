package org.matsim.contrib.smartcity.actuation.semaphore;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Id;
import org.matsim.contrib.signals.builder.SignalModelFactory;
import org.matsim.contrib.signals.data.signalgroups.v20.SignalPlanData;
import org.matsim.contrib.signals.model.DatabasedSignalPlan;
import org.matsim.contrib.signals.model.SignalController;
import org.matsim.contrib.signals.model.SignalPlan;
import org.matsim.contrib.signals.model.SignalSystem;
import org.matsim.contrib.signals.model.SignalSystemImpl;

/**
 * This factory create a instance of class indicated.
 * 
 * @author Filippo Muzzini
 *
 */
public class SmartSemaphoreModelFactory implements SignalModelFactory {
	
	private static final Logger log = Logger.getLogger(SmartSemaphoreModelFactory.class);

	@Override
	public SignalSystem createSignalSystem(Id<SignalSystem> id) {
		return new SignalSystemImpl(id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public SignalController createSignalSystemController(String controllerIdentifier, SignalSystem signalSystem) {
		Class<? extends SignalController> controller = null;
		SignalController signalControl = null;
		try {
			controller = (Class<? extends SignalController>) Class.forName(controllerIdentifier);
			log.info("Created SignalController: " + controllerIdentifier);
			signalControl = controller.newInstance();
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException("Controller " + controllerIdentifier + " not known.");
		} catch (InstantiationException e) {
			throw new IllegalArgumentException("Controller " + controllerIdentifier + " has wrong constructor.");
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		signalControl.setSignalSystem(signalSystem);
		return signalControl;		
	}

	@Override
	public SignalPlan createSignalPlan(SignalPlanData planData) {
		return new DatabasedSignalPlan(planData);
	}

}
