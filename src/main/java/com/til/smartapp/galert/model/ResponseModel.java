package com.til.smartapp.galert.model;

import java.sql.Timestamp;

public class ResponseModel {
	
	private String pingURL;
	
	private Double responseTime;
	
	private Boolean pingStatus;
	
	private Timestamp pingTimestamp;
	
	private Integer responseCode;
	
	private String responseMessage;

	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	public Integer getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(Integer responseCode) {
		this.responseCode = responseCode;
	}

	public String getPingURL() {
		return pingURL;
	}

	public void setPingURL(String pingURL) {
		this.pingURL = pingURL;
	}

	public Double getResponseTime() {
		return responseTime;
	}

	public void setResponseTime(Double responseTime) {
		this.responseTime = responseTime;
	}

	public Boolean getPingStatus() {
		return pingStatus;
	}

	public void setPingStatus(Boolean pingStatus) {
		this.pingStatus = pingStatus;
	}

	public Timestamp getPingTimestamp() {
		return pingTimestamp;
	}

	public void setPingTimestamp(Timestamp pingTimestamp) {
		this.pingTimestamp = pingTimestamp;
	}
}
