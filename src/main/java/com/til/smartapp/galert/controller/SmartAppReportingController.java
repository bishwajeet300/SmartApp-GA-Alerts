package com.til.smartapp.galert.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.til.smartapp.galert.constants.SmartAppConstants;
import com.til.smartapp.galert.service.ISmartAppReportingServices;


@Controller
@RequestMapping("/notificationservice")
public class SmartAppReportingController {

	private static final Logger logger = Logger.getLogger(SmartAppReportingController.class);
	
	@Autowired
	ISmartAppReportingServices iSmartAppReportingServices;
	
	@RequestMapping(value = "/getRealTimeUserCount", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> getRealTimeUserCount(Locale locale, Model model,HttpServletResponse response) {
		
		logger.debug("Get Real-Time GA Users");
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(SmartAppConstants.JSON_MEDIA_TYPE);
		String result = iSmartAppReportingServices.generateRealTimeReport();
		logger.info("Real-Time User Count: Status: " + result);
		return new ResponseEntity<String>(result, responseHeaders, HttpStatus.CREATED);
	}
}
