/**
 * 
 */
package org.matsim.contrib.smartcity;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

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
		Class<?> objectClass = getClassForName(name);		
		return instantiateClass(inj, objectClass);
	}
	
	private static Class<?> getClassForName(String name){
		String className = foundClassName(name);
		Class<?> objectClass = null;
		try {
			objectClass = (Class<?>) Class.forName(className);
		} catch (ClassNotFoundException e1) {
			System.err.println("Class "+className+" not found");
			e1.printStackTrace();
		}
		
		return objectClass;
	}
	
	/**
	 * Instantiate the class defined in the name using the constructor with less number
	 * of parameters and use the params specified. The non specified params are fell using
	 * injector.
	 * If the name don't contains the package, the default package is used.
	 * 
	 * @param inj injector
	 * @param name name of class
	 * @param params specified params
	 * @return instantiated object
	 */
	public static Object instantiateForNameWithParams(Injector inj, String name, Object ... params) {
		HashMap<Class<?>, Object> paramsType = new HashMap<Class<?>, Object>();
		for (Object param : params) {
			if (param != null)
				paramsType.put(param.getClass(), param);
		}
		
		Class<?> cl = getClassForName(name);
		Constructor<?> constructor = getMinConstructor(cl);
		Object[] paramsObj = new Object[constructor.getParameterTypes().length];
		int i = 0;
		for (Class<?> type : constructor.getParameterTypes()) {
			Object obj = paramsType.get(type);
			obj = obj != null ? obj : inj.getInstance(type);
			paramsObj[i] = obj;
			i++;
		}
				
		return instantiateClassWithConstructorAndParams(inj, constructor, paramsObj);
	}
	

	/**
	 * Instantiate the class using the constructor with less number of parameters
	 * 
	 * @param inj injector
	 * @param cl class
	 * @return instantiated class object
	 */
	public static Object instantiateClass(Injector inj, Class<?> cl) {
		Constructor<?> constructor = getMinConstructor(cl);
		return instantiateClassWithConstructor(inj, constructor);
	}
	
	private static Constructor<?> getMinConstructor(Class<?> cl){
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
		
		return constructor;
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
		
		return instantiateClassWithConstructorAndParams(inj, constructor, objectsParams);
	}
	
	private static Object instantiateClassWithConstructorAndParams(Injector inj, Constructor<?> constructor, Object[] params) {
		Object res = null;
		try {
			res = constructor.newInstance(params);
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
	public static String foundClassName(String name) {
		try {
			Class.forName(name);
			return name;
		} catch (ClassNotFoundException e1) {
			return DEFAULT_PACKAGE+"."+name;
		}
	}

}
