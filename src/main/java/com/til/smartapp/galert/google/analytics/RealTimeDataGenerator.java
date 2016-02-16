package com.til.smartapp.galert.google.analytics;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.api.services.analytics.model.RealtimeData;

@Component(value = "realTimeDataGenerator")
public class RealTimeDataGenerator {

	static Logger logger = Logger.getLogger(RealTimeDataGenerator.class);
	
	@Autowired
	SmartAppAnalytics smartAppAnalytics;
	
	public Integer getRealTimeUserCountReport() {
		
		logger.debug("GetRealTimeUserCountReport");
		RealtimeData realtimeUserData = smartAppAnalytics.getRealTimeUserCount();
		
		Integer realtimeUserCount = null;
		
		if(realtimeUserData != null) {
			logger.debug("getRealTimeUserCountReport : RealTimeData is not null");
			logger.debug("Total number of users: "+realtimeUserData.getTotalsForAllResults().get("rt:activeUsers"));
			realtimeUserCount = Integer.parseInt(realtimeUserData.getTotalsForAllResults().get("rt:activeUsers"));
		} else {
			logger.info("getRealTimeUserCountReport : Unable to fetch realtime count! RealtimeData is null");
		} 
		logger.debug("Realtime User Count Generated");
		return realtimeUserCount;
	}
}
