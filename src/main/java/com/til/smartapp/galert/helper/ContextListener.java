package com.til.smartapp.galert.helper;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;

public class ContextListener extends ContextLoaderListener implements ServletContextListener {

	private static final Logger logger = Logger.getLogger(ContextListener.class);

	public ContextListener() {
		super();
	}

	public ContextListener(WebApplicationContext context) {
		super(context);
	}

	public void contextInitialized(ServletContextEvent sce) {
		try{
			logger.debug("Initializing Context");
		}catch(Exception e){
			logger.error(e);
		}
	}
	
	public void contextDestroyed(ServletContextEvent sce) {
		logger.debug("Context Destroyed");
	}
}
