/**
 * 
 */
package org.matsim.contrib.smartcity.agent;

import java.util.Collection;
import java.util.LinkedList;

import org.matsim.contrib.parking.parkingsearch.evaluation.ParkingListener;
import org.matsim.contrib.parking.parkingsearch.manager.ParkingSearchManager;
import org.matsim.contrib.parking.parkingsearch.manager.WalkLegFactory;
import org.matsim.contrib.parking.parkingsearch.manager.vehicleteleportationlogic.VehicleTeleportationLogic;
import org.matsim.contrib.parking.parkingsearch.manager.vehicleteleportationlogic.VehicleTeleportationToNearbyParking;
import org.matsim.contrib.smartcity.agent.parking.FacilityWithMoreEntranceParkingManager;
import org.matsim.core.config.Config;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.mobsim.qsim.AbstractQSimModule;
import org.matsim.core.mobsim.qsim.PopulationModule;
import org.matsim.core.mobsim.qsim.QSimModule;
import com.google.inject.Provides;

/**
 * Module for SmartAgent.
 * Provides a list of plugins for MATSim simulation; in particular
 * the SmartPopulationPlugin.
 * 
 * @see SmartPopulationPlugin
 * @author Filippo Muzzini
 *
 */
public class SmartAgentModule extends AbstractModule {
	
	/**
	 * Create the plugins list with SmartPopulationPlugin
	 * 
	 * @param config1 Configuration instance
	 * @return list of plugins
	 */
	@Provides
	Collection<AbstractQSimModule> provideQSimPlugins(Config config1) {
		Collection<AbstractQSimModule> modules = new LinkedList<>(QSimModule.getDefaultQSimModules());
		modules.removeIf(PopulationModule.class::isInstance);
		modules.add(new SmartPopulationPlugin());
		return modules;
	}

	/* (non-Javadoc)
	 * @see org.matsim.core.controler.AbstractModule#install()
	 */
	@Override
	public void install() {
		bind(ParkingSearchManager.class).to(FacilityWithMoreEntranceParkingManager.class).asEagerSingleton();
		bind(WalkLegFactory.class).asEagerSingleton();
		addControlerListenerBinding().to(ParkingListener.class);
		bind(VehicleTeleportationLogic.class).to(VehicleTeleportationToNearbyParking.class);
	}

}
