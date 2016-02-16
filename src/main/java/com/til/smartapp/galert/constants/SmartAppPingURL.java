package com.til.smartapp.galert.constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class SmartAppPingURL {
	
	private static Map<String, List<Integer>> urlMap = new HashMap<String, List<Integer>>();
	private static List<Integer> timeoutList = new ArrayList<Integer>();
	
	public static Integer populatePingURL() {
		ResourceBundle pingURLMap = ResourceBundle.getBundle("pingURL");
		String pingURLKeyValPair = pingURLMap.getString("PING_URL_TIMEOUT");
		
		String[] pindURLArr = pingURLKeyValPair.split(";");
		for(String strVal : pindURLArr) {
			String[] temp = strVal.split(",");
			timeoutList.add(0, Integer.parseInt(temp[1]));
			timeoutList.add(1, Integer.parseInt(temp[2]));
			urlMap.put(temp[0], timeoutList);
		}
		return urlMap.size();
	}

	public static Map<String, List<Integer>> getPingURLMap() {
		return urlMap;
	}
}
