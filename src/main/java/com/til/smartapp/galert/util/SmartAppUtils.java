package com.til.smartapp.galert.util;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

public class SmartAppUtils {

	public static final ResourceBundle appProperties = ResourceBundle.getBundle("app");	
	public static final ResourceBundle smartappProperties = ResourceBundle.getBundle("SmartApp");
	private static final Logger logger = Logger.getLogger(SmartAppUtils.class);
	private static Map<String, String> smartappPropertiesMap = new HashMap<String, String>();
}
