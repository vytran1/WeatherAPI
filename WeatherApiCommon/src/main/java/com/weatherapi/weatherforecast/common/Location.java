package com.weatherapi.weatherforecast.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "locations")
public class Location {
	@Id
	@Column(name = "code",length = 12,nullable = false,unique = true)
	@NotNull(message = "Location code must not be null")
	private String code;
	
	@Column(name = "city_name",length = 128,nullable = false)
	@JsonProperty("city_name")
	@NotBlank(message = "Location city name must not be blank")
	private String cityName;
	
	@Column(name = "region_name",length = 128)
	@JsonProperty("region_name")
	private String regionName;
	
	
	@Column(name = "country_name",length = 64,nullable = false)
	@JsonProperty("country_name")
	@NotBlank(message = "Location country name must not be blank")
	private String countryName;
	
	@Column(name = "country_code",length = 2,nullable = false)
	@JsonProperty("country_code")
	@NotBlank(message = "Location country code must not be blank")
	private String countryCode;
	
	private boolean enabled;
	
	@JsonIgnore
	private boolean trashed;
	
	
	@OneToOne(mappedBy = "location",cascade = CascadeType.ALL)
	@PrimaryKeyJoinColumn
	@JsonIgnore
	private RealtimeWeather realtimeWeather;
	
	@OneToMany(mappedBy = "id.location",cascade = CascadeType.ALL,orphanRemoval = true)
	private List<HourlyWeather> hourlyweathers = new ArrayList<>();
	
	@OneToMany(mappedBy = "id.location",cascade = CascadeType.ALL,orphanRemoval = true)
	private List<DailyWeather> dailyweathers = new ArrayList<>();
	
	public Location() {
		
	}
	
	
	
	
	
	public Location(String code) {
		super();
		this.code = code;
	}





	public Location(String cityName, String regionName, String countryName, String countryCode) {
		super();
		this.cityName = cityName;
		this.regionName = regionName;
		this.countryName = countryName;
		this.countryCode = countryCode;
	}
    
	
	public Location(String code, String cityName, String regionName, String countryName, String countryCode) {
		this(cityName,regionName,countryName,countryCode);
		this.code = code;
	}
	
	public Location(String code, String cityName, String regionName, String countryName, String countryCode,boolean enabled) {
		this(code,cityName,regionName,countryName,countryCode);
		this.enabled = enabled;
	}
    


	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	public String getRegionName() {
		return regionName;
	}
	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}
	public String getCountryName() {
		return countryName;
	}
	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public boolean isTrashed() {
		return trashed;
	}
	public void setTrashed(boolean trashed) {
		this.trashed = trashed;
	}
	
	
	
	
	
	
	public List<DailyWeather> getDailyweathers() {
		return dailyweathers;
	}



	public void setDailyweathers(List<DailyWeather> dailyweathers) {
		this.dailyweathers = dailyweathers;
	}



	public RealtimeWeather getRealtimeWeather() {
		return realtimeWeather;
	}
	public void setRealtimeWeather(RealtimeWeather realtimeWeather) {
		this.realtimeWeather = realtimeWeather;
	}
	
	
	
	
	public List<HourlyWeather> getHourlyweathers() {
		return hourlyweathers;
	}



	public void setHourlyweathers(List<HourlyWeather> hourlyweathers) {
		this.hourlyweathers = hourlyweathers;
	}

    public Location code(String code) {
    	setCode(code);
    	return this;
    }

	@Override
	public int hashCode() {
		return Objects.hash(code);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Location other = (Location) obj;
		return Objects.equals(code, other.code);
	}



	@Override
	public String toString() {
		return this.getCityName() + ", " + this.getRegionName() + ", " + this.getCountryName() + "," + this.countryCode;
	}
	
	
	public void copyFieldsFromLocation(Location another) {
		setCode(another.getCode());
		setCityName(another.getCityName());
		setCountryCode(another.getCountryCode());
		setCountryName(another.getCountryName());
		setEnabled(another.isEnabled());
//		setTrashed(another.isTrashed());
	}
	
	
	public void copyAllFieldsFromLocation(Location another) {
		setCode(another.getCode());
		setCityName(another.getCityName());
		setCountryCode(another.getCountryCode());
		setCountryName(another.getCountryName());
		setEnabled(another.isEnabled());
		setTrashed(another.isTrashed());
	}
	
	
	
	
	
	
}
