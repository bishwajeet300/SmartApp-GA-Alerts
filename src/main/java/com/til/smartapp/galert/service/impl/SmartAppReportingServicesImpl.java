package com.til.smartapp.galert.service.impl;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.til.smartapp.galert.constants.SmartAppPingURL;
import com.til.smartapp.galert.google.analytics.RealTimeDataGenerator;
import com.til.smartapp.galert.model.ResponseModel;
import com.til.smartapp.galert.service.ISmartAppReportingServices;

import flowctrl.integration.slack.SlackClientFactory;
import flowctrl.integration.slack.type.Attachment;
import flowctrl.integration.slack.type.Color;
import flowctrl.integration.slack.type.Payload;
import flowctrl.integration.slack.webhook.SlackWebhookClient;

@Service
public class SmartAppReportingServicesImpl implements ISmartAppReportingServices {
	
	static Logger logger = Logger.getLogger(SmartAppReportingServicesImpl.class);
	
	@Autowired
	RealTimeDataGenerator realTimeDataGenerator;
	
	@Override
	public String generateRealTimeReport() {
		logger.debug("generateRealTimeReport");
		List<ResponseModel> responseList = new ArrayList<ResponseModel>();
		List<ResponseModel> faultyURLList = new ArrayList<ResponseModel>();
		int status = 10;
		String statusResult = "";
		Integer realtimeUserCount = realTimeDataGenerator.getRealTimeUserCountReport();
		if(realtimeUserCount != null) {
			logger.debug("generateRealTimeReport: User Count: " + realtimeUserCount);
			if(realtimeUserCount >= 200) {
				logger.info("generateRealTimeReport: User count exceeding limit of 250.");
				logger.info("generateRealTimeReport: Initiating server ping module!!");
				responseList = pingURLs();
				logger.info("generateRealTimeReport: Server ping module ended!!");
				logger.info("generateRealTimeReport: Pinged " + responseList.size() + " URLs.");
				
				faultyURLList = filterFaultyURL(responseList);
				
				if(faultyURLList.size() > 0) {
					alertOnSlack(faultyURLList, realtimeUserCount);
				}
				
				status = 0;
			} else {
				logger.info("generateRealTimeReport: User count within limit of 300.");
				logger.info("generateRealTimeReport: Skipping server ping module!!");
				status = 1;
			}
		} else {
			logger.info("generateRealTimeReport: User count cannot be determined");
			logger.info("generateRealTimeReport: Skipping server ping module!!");
			status = 2;
		}
		switch(status) {
			case 0:
				statusResult = "URLs pinged successfully";
				break;
			case 1:
				statusResult = "User count less than 250, ping module skipped.";
				break;
			case 2:
				statusResult = "User count cannot be determined, ping module skipped.";
				break;
			case 10:
				statusResult = "Error occured while ping!!";
		}
		return statusResult;
	}

	private List<ResponseModel> pingURLs() {
		List<ResponseModel> responseList = new ArrayList<ResponseModel>();
		Map<String, List<Integer>> pingURLMap = SmartAppPingURL.getPingURLMap();
		for(Map.Entry<String, List<Integer>> entry : pingURLMap.entrySet()) {
			logger.info("Pinging: " + entry.getKey());
			responseList.add(ping(entry.getKey(), entry.getValue().get(0), entry.getValue().get(1)));
		}
		return responseList;
	}
	
	public static ResponseModel ping(String url, int connectTimeout, int readTimeout) {
		ResponseModel response = new ResponseModel();
		Date date= new Date();
		Boolean status = Boolean.FALSE;
		int responseCode = 0;
		String responseMessage = "";
		url = url.replaceFirst("^https", "http"); // Otherwise an exception may be thrown on invalid SSL certificates.
		long nanoStart = System.nanoTime();
		long nanoStop = 0;
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
		    connection.setConnectTimeout(connectTimeout);
		    connection.setReadTimeout(readTimeout);
		    connection.setRequestMethod("HEAD");
		    responseCode = connection.getResponseCode();
		    responseMessage = connection.getResponseMessage();
		    if(200 <= responseCode && responseCode <= 399) {
		    	status = Boolean.TRUE;
		    } else {
		    	status = Boolean.FALSE;
		    }
		    nanoStop = System.nanoTime();
		} catch(SocketTimeoutException exception) {
			logger.info("SocketTimeoutException for " + url);
			status = Boolean.FALSE;
			nanoStop = System.nanoTime();
			responseMessage = exception.getMessage();
		} catch (IOException exception) {
			status = Boolean.FALSE;
			nanoStop = System.nanoTime();
			responseMessage = exception.getMessage();
		}
		response.setPingURL(url);
		response.setResponseTime((nanoStop-nanoStart)/ (double) 1000000);
		response.setPingStatus(status);
		response.setPingTimestamp(new Timestamp(date.getTime()));
		response.setResponseCode(responseCode);
		response.setResponseMessage(responseMessage);
		logger.info("Ping Result: Response: " + responseCode + "-" + responseMessage + ", URL: " + response.getPingURL() + ", ResponseTime: " + response.getResponseTime() + "ms, PingSuccess: " + response.getPingStatus() + ", Timestamp: " + response.getPingTimestamp());
		return response;
	}

	public static List<ResponseModel> filterFaultyURL(List<ResponseModel> responseModelsList) {
		List<ResponseModel> faultyURLList = new ArrayList<ResponseModel>();
		for(ResponseModel responseModel: responseModelsList) {
			if(responseModel.getPingStatus() == Boolean.FALSE || responseModel.getResponseMessage().contains("timeout") || responseModel.getResponseMessage().contains("refused") || responseModel.getResponseTime() > 5000) {
				logger.info("Faulty URLS: " + responseModel.getPingURL() + ", ResponseCode: " + responseModel.getResponseCode() + ", ResponseMessage: " + responseModel.getResponseMessage() + ", ResponseTime: " + responseModel.getResponseTime());
				faultyURLList.add(responseModel);
			}
		}
		return faultyURLList;
	}

	public static Boolean alertOnSlack(List<ResponseModel> faultyURLs, int realtimeUserCount) {
		SlackWebhookClient webhookClient = SlackClientFactory.createWebhookClient("https://hooks.slack.com/services/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");
		String temp = "\n";
		for(ResponseModel res : faultyURLs) {
			temp += "*URL:* " + res.getPingURL() + "\n*ResponseMsg:* `" + res.getResponseMessage() + "`, *ResponseTime:* `" + res.getResponseTime() + "ms`\n\n";
		}
		
		Attachment attachment = new Attachment();
		attachment.setColor(Color.DANGER);
		attachment.setTitle("Real-time Alerts");
		
		Payload slackPayload = new Payload();
		slackPayload.addAttachment(attachment);
		slackPayload.setChannel("@bishwajeet");
		slackPayload.setText("*Active user count exceeded limit:* `"+ realtimeUserCount +"`\n" + temp);
		slackPayload.setMrkdwn(Boolean.TRUE);
		slackPayload.setIcon_emoji(":skull_and_crossbones:");
		slackPayload.setUsername("Reat-Time Ping Alerts");
		
		webhookClient.post(slackPayload);
		return null;
	}
}
