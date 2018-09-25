/**
 * 
 */
package org.matsim.contrib.smartcity.restriction;

import org.matsim.api.core.v01.network.Network;
import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.core.router.util.LeastCostPathCalculatorFactory;
import org.matsim.core.router.util.TravelDisutility;
import org.matsim.core.router.util.TravelTime;

/**
 * Factory for creation of LeastCostPathCalculator that is restrictions aware
 * @author Filippo Muzzini
 *
 */
public class LeastCostPathCalculatorWithRestrictionsFactory implements LeastCostPathCalculatorFactory {

	/* (non-Javadoc)
	 * @see org.matsim.core.router.util.LeastCostPathCalculatorFactory#createPathCalculator(org.matsim.api.core.v01.network.Network, org.matsim.core.router.util.TravelDisutility, org.matsim.core.router.util.TravelTime)
	 */
	@Override
	public LeastCostPathCalculator createPathCalculator(Network network, TravelDisutility travelCosts,
			TravelTime travelTimes) {
		return new RestrictionsDijkstra(network, travelCosts, travelTimes);
	}

}
