/**
 * 
 */
package org.matsim.contrib.smartcity.perception;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.matsim.core.utils.io.MatsimXmlParser;
import org.xml.sax.Attributes;

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
	
	private List<CameraData> cameraList = new ArrayList<CameraData>();

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
		if (name.equals(CAMERA_TAG)){
			String cameraLink = atts.getValue(CAMERA_LINK);
			String cameraClass = atts.getValue(CAMERA_CLASS);
			String cameraId = atts.getValue(CAMERA_ID);
			CameraData cameraData = new CameraData(cameraClass, cameraLink, cameraId);
			this.cameraList.add(cameraData);
		}
		
	}

	/* (non-Javadoc)
	 * @see org.matsim.core.utils.io.MatsimXmlParser#endTag(java.lang.String, java.lang.String, java.util.Stack)
	 */
	@Override
	public void endTag(String name, String content, Stack<String> context) {
		
	}

}
