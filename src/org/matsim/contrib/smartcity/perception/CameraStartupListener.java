/**
 * 
 */
package org.matsim.contrib.smartcity.perception;

import java.net.URL;
import java.util.List;

import org.matsim.contrib.smartcity.InstantationUtils;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigGroup;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.events.StartupEvent;
import org.matsim.core.controler.listener.StartupListener;

import com.google.inject.Injector;

/**
 * Listener that initialize the cameras using the xml file
 * 
 * @author Filippo Muzzini
 *
 */
public class CameraStartupListener implements StartupListener {

	/* (non-Javadoc)
	 * @see org.matsim.core.controler.listener.StartupListener#notifyStartup(org.matsim.core.controler.events.StartupEvent)
	 */
	@Override
	public void notifyStartup(StartupEvent event) {
		List<CameraData> cameraList = getCameraListFromConfig(event.getServices().getConfig());
		Injector inj = event.getServices().getInjector();
		for (CameraData camera : cameraList) {
			String className = camera.getClassName();
			Object[] params = new Object[] {camera.getCameraId(), camera.getLinkId()};
			InstantationUtils.instantiateForNameWithParams(inj, className, params);
		}
	}

	/**
	 * @return list of cameras readed in the file
	 */
	private List<CameraData> getCameraListFromConfig(Config config) {
		PerceptionConfigGroup group = ConfigUtils.addOrGetModule(config, PerceptionConfigGroup.GRUOPNAME, PerceptionConfigGroup.class); 
		String fileName = group.getCameraFile();
		CameraXMLReader reader = new CameraXMLReader();
		URL fileURL = ConfigGroup.getInputFileURL(config.getContext(), fileName);
		reader.readFile(fileURL.getFile());
		return reader.getCameraList();
	}

}
