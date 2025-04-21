package com.weatherapi.weatherforecast;

import java.util.ArrayList;
//import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class ErrorDTO {
	private Date timestamp;
	private Integer status;
	private String path;
	private List<String> error = new ArrayList<>();

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public List<String> getError() {
		return error;
	}

	public void setError(List<String> error) {
		this.error = error;
	}
   
    public void addError(String error) {
    	this.error.add(error);
    }
}
