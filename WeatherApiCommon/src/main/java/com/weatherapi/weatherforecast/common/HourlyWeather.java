package com.weatherapi.weatherforecast.common;

import java.util.Objects;
import java.util.Set;

import org.hibernate.validator.constraints.Range;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "weather_hourly")
public class HourlyWeather {
	@EmbeddedId
    private HourlyWeatherId id = new HourlyWeatherId();
	
	@Range(min = -50,max = 50, message="Temperature must be in the range of -50 to 50 celcius degree")
	private Integer temperature;
	private Integer precipitation;
	
	@Column(length = 50)
	private String  status;

	

	public HourlyWeatherId getId() {
		return id;
	}

	public void setId(HourlyWeatherId id) {
		this.id = id;
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
	
    public HourlyWeather temperature(Integer temperature ) {
         setTemperature(temperature);
         return this;
    }
    
    public HourlyWeather precipitation(Integer precipitation) {
    	setPrecipitation(precipitation);
    	return this;
    }
    
    public HourlyWeather status(String status) {
    	setStatus(status);
    	return this;
    }
    
    public HourlyWeather location(Location location) {
    	this.id.setLocation(location);
    	return this;
    }
    
    public HourlyWeather hourOfDay(Integer hourOfDay) {
    	this.id.setHourOfDay(hourOfDay);
    	return this;
    }
    
    
    public HourlyWeather id(Location location,Integer hourOfDay) {
    	this.id.setHourOfDay(hourOfDay);
    	this.id.setLocation(location);
    	return this;
    }

	@Override
	public String toString() {
		return "HourlyWeather [hourOfDay=" + id.getHourOfDay() + ", temperature=" + temperature + ", precipitation=" + precipitation
				+ ", status=" + status + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HourlyWeather other = (HourlyWeather) obj;
		return Objects.equals(id, other.id);
	}
	 
    public HourlyWeather copy() {
    	HourlyWeather shalowCopy = new HourlyWeather();
    	shalowCopy.setId(id);
    	return shalowCopy;
    }
    
    
}
