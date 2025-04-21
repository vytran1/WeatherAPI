package com.weatherapi.weatherforecast.common;

import java.util.Date;
import java.util.Objects;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
@Entity
@Table(name = "realtime_weather")
public class RealtimeWeather {
	
	
	@Id
	@Column(name = "location_code")
	@JsonIgnore
	private String locationCode;
	
	@Range(min = -50,max = 50, message="Temperature must be in the range of -50 to 50 celcius degree")
	private Integer temperature;
	
	@Range(min = 0,max = 100, message="Himidity must be in the range of 0 to 100 celcius degree")
	private Integer humidity;
	
	@Range(min = 0,max = 100, message="Precipitation must be in the range of 0 to 100 celcius degree")
	private Integer precipitation;
	
	@JsonProperty("wind_speed")
	@Range(min = 0,max = 200, message="Wind Speed must be in the range of 0 to 100 celcius degree")
	private Integer windSpeed;
	
	@Column(name = "status",length = 50)
	@NotBlank(message = "Status must not be blank")
	@Length(min = 3, max = 50, message = "Status must be in range of 3 to 50 characters ")
	private String status;
	
	
	@JsonProperty("last_updated")
	@JsonIgnore
	private Date lastUpdated;
    
	
	@OneToOne()
	@JoinColumn(name = "location_code",referencedColumnName = "code")
	@MapsId
	@JsonIgnore
	private Location location;


	public String getLocationCode() {
		return locationCode;
	}


	public void setLocationCode(String locationCode) {
		this.locationCode = locationCode;
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


	public Location getLocation() {
		return location;
	}


	public void setLocation(Location location) {
		this.locationCode = location.getCode();
		this.location = location;
	}


	@Override
	public String toString() {
		return "RealtimeWeather [locationCode=" + locationCode + ", temperature=" + temperature + ", humidity="
				+ humidity + ", precipitation=" + precipitation + ", windSpeed=" + windSpeed + ", status=" + status
				+ ", lastUpdated=" + lastUpdated + "]";
	}


	@Override
	public int hashCode() {
		return Objects.hash(locationCode);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RealtimeWeather other = (RealtimeWeather) obj;
		return Objects.equals(locationCode, other.locationCode);
	}
	
	
	
}
