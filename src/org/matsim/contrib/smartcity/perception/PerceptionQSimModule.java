package org.matsim.contrib.smartcity.perception;

import org.matsim.contrib.smartcity.InstantationUtils;
import org.matsim.contrib.smartcity.perception.wrapper.ActivePerceptionWrapper;
import org.matsim.contrib.smartcity.perception.wrapper.PassivePerceptionWrapper;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.AbstractModule;

/**
 * Module that instantiates the wrapper using reflection
 * and bind the class to the implemented interfaces.
 * 
 * @author Filippo Muzzini
 *
 */
public class PerceptionQSimModule extends AbstractModule {
	
	private static final String DEFAULT_WRAPPER_CLASS = "perception.wrapper.ActivePerceptionWrapperImpl";
	
	private Class<PassivePerceptionWrapper> perceptionClass;
	

	@SuppressWarnings("unchecked")
	public PerceptionQSimModule(Config config) {
		super();
		
		PerceptionConfigGroup cameraConfig = ConfigUtils.addOrGetModule(config, PerceptionConfigGroup.GRUOPNAME, PerceptionConfigGroup.class);
		String className = cameraConfig.getWrapperClass();
		className = className != null ? className : DEFAULT_WRAPPER_CLASS;
		className = InstantationUtils.foundClassName(className);
		try {
			this.perceptionClass = (Class<PassivePerceptionWrapper>) Class.forName(className);
		} catch (ClassNotFoundException e) {
			System.err.println("Classe non trovata");
			e.printStackTrace();
		}
	}

	@Override
	public void install() {		
		//bind the class to interfaces
		bind(PassivePerceptionWrapper.class).to(this.perceptionClass);
		try {
			Class<? extends ActivePerceptionWrapper> activeClass = ActivePerceptionWrapper.class;
			activeClass = this.perceptionClass.asSubclass(activeClass);
			bind(ActivePerceptionWrapper.class).to(activeClass);
		} catch (ClassCastException e) {
		}
		bind(this.perceptionClass).asEagerSingleton();
		addEventHandlerBinding().to(this.perceptionClass);
		
		addControlerListenerBinding().to(CameraStartupListener.class);
	}

}
