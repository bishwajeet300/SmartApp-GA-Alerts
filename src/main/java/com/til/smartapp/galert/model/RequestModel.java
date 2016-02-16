package com.til.smartapp.galert.model;

public class RequestModel {
	
	private String pingURL;
	
	private Integer connectionTimeout;
	
	private Integer readTimeout;

	public String getPingURL() {
		return pingURL;
	}

	public void setPingURL(String pingURL) {
		this.pingURL = pingURL;
	}

	public Integer getConnectionTimeout() {
		return connectionTimeout;
	}

	public void setConnectionTimeout(Integer connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public Integer getReadTimeout() {
		return readTimeout;
	}

	public void setReadTimeout(Integer readTimeout) {
		this.readTimeout = readTimeout;
	}
}
