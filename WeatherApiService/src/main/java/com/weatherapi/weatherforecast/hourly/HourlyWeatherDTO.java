package com.weatherapi.weatherforecast.hourly;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.weatherapi.weatherforecast.common.HourlyWeather;
import com.weatherapi.weatherforecast.common.Location;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;

public class HourlyWeatherDTO {
	@JsonProperty("hour_of_day")
	private Integer hourOfDay;
	
	@Range(min = -50,max = 50, message="Temperature must be in the range of -50 to 50 celcius degree")
	private Integer temperature;
	
	@Range(min = 0,max = 100, message="Precipitation must be in the range of 0 to 100 celcius degree")
	private Integer precipitation;
	
	@NotBlank(message = "Status must not be blank")
	@Length(min = 3, max = 50, message = "Status must be in range of 3 to 50 characters ")
	private String  status;

	public int getHourOfDay() {
		return hourOfDay;
	}

	public void setHourOfDay(int hourOfDay) {
		this.hourOfDay = hourOfDay;
	}

	public Integer getTemperature() {
		return temperature;
	}

	public void setTemperature(Integer temperature) {
		this.temperature = temperature;
	}

	public Integer getPrecipitation() {
		return precipitation;
	}

	public void setPrecipitation(Integer precipitation) {
		this.precipitation = precipitation;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	 public HourlyWeatherDTO temperature(Integer temperature ) {
         setTemperature(temperature);
         return this;
    }
    
    public HourlyWeatherDTO precipitation(Integer precipitation) {
    	setPrecipitation(precipitation);
    	return this;
    }
    
    public HourlyWeatherDTO status(String status) {
    	setStatus(status);
    	return this;
    }
    
    public HourlyWeatherDTO hourOfDay(Integer hourOfDay) {
    	this.setHourOfDay(hourOfDay);
    	return this;
    }
	
}
