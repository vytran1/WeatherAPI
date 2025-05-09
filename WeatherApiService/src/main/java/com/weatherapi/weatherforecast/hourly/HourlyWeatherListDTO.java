package com.weatherapi.weatherforecast.hourly;

import java.util.ArrayList;
import java.util.List;

import org.springframework.hateoas.RepresentationModel;

public class HourlyWeatherListDTO extends RepresentationModel<HourlyWeatherListDTO> {
    private String location;
    private List<HourlyWeatherDTO> hourlyForecast = new ArrayList<>();
    
    
    
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public List<HourlyWeatherDTO> getHourlyForecast() {
		return hourlyForecast;
	}
	public void setHourlyForecast(List<HourlyWeatherDTO> hourlyForecast) {
		this.hourlyForecast = hourlyForecast;
	}
    
    public void addHourlyWeatherDTO(HourlyWeatherDTO dto) {
    	this.hourlyForecast.add(dto);
    }
}
