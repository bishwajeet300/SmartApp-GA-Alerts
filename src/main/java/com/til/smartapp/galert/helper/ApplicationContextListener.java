package com.til.smartapp.galert.helper;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.til.smartapp.galert.constants.SmartAppPingURL;

public class ApplicationContextListener implements	ApplicationListener<ContextRefreshedEvent> {
	
	private static Logger logger = Logger.getLogger(ApplicationContextListener.class);

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		//ApplicationContext applicationContext = event.getApplicationContext();
		
		logger.info("ApplicationContextListener : Initializing Ping URLs!!!");
		int mapSize = SmartAppPingURL.populatePingURL();
		logger.info("ApplicationContextListener : Ping URL Map initialized with size: "+mapSize);
	}

}
