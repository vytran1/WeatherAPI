package com.weatherapi.weatherforecast.realtime;

import java.util.Date;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;
import com.weatherapi.weatherforecast.common.RealtimeWeather;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

public class RealtimeWeatherDTO extends RepresentationModel<RealtimeWeatherDTO> {
	
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String location;
	
	
	@Range(min = -50,max = 50, message="Temperature must be in the range of -50 to 50 celcius degree")
	private Integer temperature;
	
	@Range(min = 0,max = 100, message="Himidity must be in the range of 0 to 100 celcius degree")
	private Integer humidity;
	
	@Range(min = 0,max = 100, message="Precipitation must be in the range of 0 to 100 celcius degree")
	private Integer precipitation;
	
	@JsonProperty("wind_speed")
	@Range(min = 0,max = 200, message="Wind Speed must be in the range of 0 to 100 celcius degree")
	private Integer windSpeed;
	
	@NotBlank(message = "Status must not be blank")
	@Length(min = 3, max = 50, message = "Status must be in range of 3 to 50 characters ")
	private String status;
	
	
	@JsonProperty("last_updated")
	@JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
	private Date lastUpdated;
    
	
	


	public String getLocation() {
		return location;
	}


	public void setLocation(String locationCode) {
		this.location = locationCode;
	}


	public Integer getTemperature() {
		return temperature;
	}


	public void setTemperature(Integer temperature) {
		this.temperature = temperature;
	}


	public Integer getHumidity() {
		return humidity;
	}


	public void setHumidity(Integer humidity) {
		this.humidity = humidity;
	}


	


	public Integer getPrecipitation() {
		return precipitation;
	}


	public void setPrecipitation(Integer precipitation) {
		this.precipitation = precipitation;
	}


	public Integer getWindSpeed() {
		return windSpeed;
	}


	public void setWindSpeed(Integer windSpeed) {
		this.windSpeed = windSpeed;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public Date getLastUpdated() {
		return lastUpdated;
	}


	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}


	
	
	
	
}
