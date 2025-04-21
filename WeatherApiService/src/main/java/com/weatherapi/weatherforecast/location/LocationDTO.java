package com.weatherapi.weatherforecast.location;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import com.fasterxml.jackson.annotation.JsonProperty;
@Relation(collectionRelation = "locations")
public class LocationDTO extends RepresentationModel<LocationDTO> {
   @JsonProperty("code")
   private String code;
   
   @JsonProperty("city_name")
   private String cityName;
   
   @JsonProperty("region_name")
   private String regionName;
   
   @JsonProperty("country_name")
   private String countryName;
   
   @JsonProperty("country_code")
   private String countryCode;
   
   private Boolean enabled;

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

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

}
