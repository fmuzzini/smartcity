/**
 * 
 */
package org.matsim.contrib.smartcity.restriction;

import org.matsim.core.controler.AbstractModule;
import org.matsim.core.network.algorithms.NetworkTurnInfoBuilderI;
import org.matsim.contrib.smartcity.scenariocreation.RestrictionsFromOSM;

/**
 * Module that introduce restrictions in the network
 * The network file must have the proper attributes
 * 
 * @see RestrictionsFromOSM
 * @author Filippo Muzzini
 *
 */
public class RestrictionsModule extends AbstractModule {

	/* (non-Javadoc)
	 * @see org.matsim.core.controler.AbstractModule#install()
	 */
	@Override
	public void install() {
		if (getConfig().controler().isLinkToLinkRoutingEnabled()) {
			bind(NetworkTurnInfoBuilderI.class).to(NetworkWithRestrictionTurnInfoBuilder.class);
			bind(NetworkWithRestrictionTurnInfoBuilder.class).asEagerSingleton();
		}
	}

}
