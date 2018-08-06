package org.matsim.contrib.smartcity.perception;

import java.lang.reflect.InvocationTargetException;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Network;
import org.matsim.contrib.smartcity.perception.wrapper.ActivePerceptionWrapper;
import org.matsim.contrib.smartcity.perception.wrapper.PassivePerceptionWrapper;
import org.matsim.core.config.Config;
import org.matsim.core.controler.AbstractModule;

import com.google.inject.Provider;

/**
 * Module that instantiates the ActivePerceptionWrapper using reflection
 * and bind the class to the implemented interfaces.
 * This instance must be a singleton so this class is also a provider for the
 * ActivePerceptionWrapper class.
 * 
 * @author Filippo Muzzini
 * 
 * @see AbstractModule
 * @see Provider
 *
 */
public class PerceptionQSimModule extends AbstractModule implements Provider<ActivePerceptionWrapper>{
	
	private ActivePerceptionWrapper perceptionInstance;
	private Scenario scenario;
	private Network network;
	private Class<ActivePerceptionWrapper> perceptionClass;
	
	@SuppressWarnings("unchecked")
	public PerceptionQSimModule(Config config, Scenario scenario) {
		super();
		this.scenario = scenario;
		this.network = scenario.getNetwork();
		
		//TODO dal config si ricava la classe
		String className = "org.matsim.contrib.smartcity.perception.wrapper.ActivePerceptionWrapperImpl";
		try {
			this.perceptionClass = (Class<ActivePerceptionWrapper>) Class.forName(className);
		} catch (ClassNotFoundException e) {
			System.err.println("Classe non trovata");
			e.printStackTrace();
		}
	}

	@Override
	public void install() {		
		//bind the class to interfaces
		bind(PassivePerceptionWrapper.class).toProvider(this);
		bind(ActivePerceptionWrapper.class).toProvider(this);
		addEventHandlerBinding().to(PassivePerceptionWrapper.class);
	}


	@Override
	public ActivePerceptionWrapper get() {
		//instantiates the class if null
		if (perceptionInstance == null) {
			try {
				try {
					this.perceptionInstance = this.perceptionClass.getConstructor(Network.class, Scenario.class).newInstance(network, scenario);
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (InstantiationException e) {
				System.err.println("La classe non può essere instanziata");
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				System.err.println("Accesso illegale alla classe");
				e.printStackTrace();
			}
		}
		
		return this.perceptionInstance;
	}

}
