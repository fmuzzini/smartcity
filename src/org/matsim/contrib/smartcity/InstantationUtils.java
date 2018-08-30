/**
 * 
 */
package org.matsim.contrib.smartcity;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.google.inject.Injector;

/**
 * Class with methods that help the instantiation of class
 *  
 * @author Filippo Muzzini
 *
 */
public class InstantationUtils {
	
	/**
	 * The class is searched in this package
	 */
	private static final String DEFAULT_PACKAGE = "org.matsim.contrib.smartcity";
	
	/**
	 * Instantiate the class defined in the name using the constructor with less number
	 * of parameters
	 * If the name don't contains the package, the default package is used.
	 * 
	 * @param inj injector
	 * @param name name of class
	 * @return instantiated class object
	 */
	public static Object instantiateForName(Injector inj, String name) {
		String className = foundClassName(name);
		Class<?> objectClass = null;
		try {
			objectClass = (Class<?>) Class.forName(className);
		} catch (ClassNotFoundException e1) {
			System.err.println("Class "+className+" not found");
			e1.printStackTrace();
		}
		
		return instantiateClass(inj, objectClass);
	}
	

	/**
	 * Instantiate the class using the constructor with less number of parameters
	 * 
	 * @param inj injector
	 * @param cl class
	 * @return instantiated class object
	 */
	public static Object instantiateClass(Injector inj, Class<?> cl) {
		Constructor<?>[] constrs = cl.getConstructors();
		int min = Integer.MAX_VALUE;
		Constructor<?> constructor = null;
		for (Constructor<?> c : constrs) {
			int n = c.getParameterTypes().length;
			if (n <= min) {
				min = n;
				constructor = c;
			}
		}
		
		return instantiateClassWithConstructor(inj, constructor);
	}
	
	/**
	 * Instantiate the class using specified constructor
	 * 
	 * @param inj injector
	 * @param constructor constructor
	 * @return instantiated class object
	 */
	public static Object instantiateClassWithConstructor(Injector inj, Constructor<?> constructor) {
		Class<?>[] params = constructor.getParameterTypes();
		Object[] objectsParams = getParams(inj, params);
		Object res = null;
		try {
			res = constructor.newInstance(objectsParams);
			inj.injectMembers(res);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
		return res;
	}
	
	/**
	 * Instantiate the params specified in params usig injector
	 * 
	 * @param inj injector
	 * @param params list of parmams' class
	 * @return instantiated parmas' object
	 */
	private static Object[] getParams(Injector inj, Class<?>[] params){
		int n = params.length;
		Object[] res = new Object[n];
		for (int i=0; i<n; i++) {
			res[i] = inj.getInstance(params[i]);
		}
		
		return res;
	}
	
	/**
	 * Determinate if class name have specified the package or no.
	 * If no return the class name with default package.
	 * @param name name of class
	 * @return name of class with package
	 */
	private static String foundClassName(String name) {
		try {
			Class.forName(name);
			return name;
		} catch (ClassNotFoundException e1) {
			return DEFAULT_PACKAGE+"."+name;
		}
	}

}
