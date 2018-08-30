/**
 * 
 */
package org.matsim.contrib.smartcity.agent;

import java.util.ArrayList;
import java.util.Collection;

import org.matsim.contrib.parking.parkingsearch.evaluation.ParkingListener;
import org.matsim.contrib.parking.parkingsearch.manager.FacilityBasedParkingManager;
import org.matsim.contrib.parking.parkingsearch.manager.ParkingSearchManager;
import org.matsim.contrib.parking.parkingsearch.manager.WalkLegFactory;
import org.matsim.contrib.parking.parkingsearch.manager.vehicleteleportationlogic.VehicleTeleportationLogic;
import org.matsim.contrib.parking.parkingsearch.manager.vehicleteleportationlogic.VehicleTeleportationToNearbyParking;
import org.matsim.core.config.Config;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.mobsim.qsim.AbstractQSimPlugin;
import org.matsim.core.mobsim.qsim.ActivityEnginePlugin;
import org.matsim.core.mobsim.qsim.TeleportationPlugin;
import org.matsim.core.mobsim.qsim.changeeventsengine.NetworkChangeEventsPlugin;
import org.matsim.core.mobsim.qsim.messagequeueengine.MessageQueuePlugin;
import org.matsim.core.mobsim.qsim.pt.TransitEnginePlugin;
import org.matsim.core.mobsim.qsim.qnetsimengine.QNetsimEnginePlugin;
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
	Collection<AbstractQSimPlugin> provideQSimPlugins(Config config1) {
		final Collection<AbstractQSimPlugin> plugins = new ArrayList<>();
		plugins.add(new MessageQueuePlugin(config1));
		plugins.add(new ActivityEnginePlugin(config1));
		plugins.add(new QNetsimEnginePlugin(config1));
		if (config1.network().isTimeVariantNetwork()) {
			plugins.add(new NetworkChangeEventsPlugin(config1));
		}
		if (config1.transit().isUseTransit() && config1.transit().isUsingTransitInMobsim() ) {
			plugins.add(new TransitEnginePlugin(config1));
		}
		plugins.add(new TeleportationPlugin(config1));
		plugins.add(new SmartPopulationPlugin(config1));
		return plugins;
	}

	/* (non-Javadoc)
	 * @see org.matsim.core.controler.AbstractModule#install()
	 */
	@Override
	public void install() {
		bind(ParkingSearchManager.class).to(FacilityBasedParkingManager.class).asEagerSingleton();
		bind(WalkLegFactory.class).asEagerSingleton();
		addControlerListenerBinding().to(ParkingListener.class);
		bind(VehicleTeleportationLogic.class).to(VehicleTeleportationToNearbyParking.class);
	}

}
