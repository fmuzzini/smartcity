/**
 * 
 */
package org.matsim.contrib.smartcity.perception;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.utils.io.MatsimXmlParser;
import org.xml.sax.Attributes;

import com.google.inject.Inject;

/**
 * Reader for cameras' xml
 * @author Filippo Muzzini
 *
 */
public class CameraXMLReader extends MatsimXmlParser {
	
	private static final String CAMERA_TAG = "camera";
	private static final String CAMERA_ID = "id";
	private static final String CAMERA_LINK = "link";
	private static final String CAMERA_CLASS = "class";
	private static final String CAMERAS_TAG = "cameras";
	private static final String ALL = "all";
	private static final String TRUE = "true";
	
	private List<CameraData> cameraList = new ArrayList<CameraData>();
	@Inject private Network network;

	/**
	 * @return list of cameras
	 */
	public List<CameraData> getCameraList() {
		return cameraList;
	}

	/* (non-Javadoc)
	 * @see org.matsim.core.utils.io.MatsimXmlParser#startTag(java.lang.String, org.xml.sax.Attributes, java.util.Stack)
	 */
	@Override
	public void startTag(String name, Attributes atts, Stack<String> context) {
		switch (name) {
			case CAMERA_TAG:
				String cameraLink = atts.getValue(CAMERA_LINK);
				String cameraClass = atts.getValue(CAMERA_CLASS);
				String cameraId = atts.getValue(CAMERA_ID);
				CameraData cameraData = new CameraData(cameraClass, cameraLink, cameraId);
				this.cameraList.add(cameraData);
				break;
			case CAMERAS_TAG:
				String all = atts.getValue(ALL);
				String cameraClass_ = atts.getValue(CAMERA_CLASS);
				if (all.equals(TRUE)) {
					for (Link link : network.getLinks().values()) {
						CameraData cameraData_ = new CameraData(cameraClass_, link.getId().toString(), link.getId().toString());
						this.cameraList.add(cameraData_);
					}
				}
				
		}
		
	}

	/* (non-Javadoc)
	 * @see org.matsim.core.utils.io.MatsimXmlParser#endTag(java.lang.String, java.lang.String, java.util.Stack)
	 */
	@Override
	public void endTag(String name, String content, Stack<String> context) {
		
	}

}
