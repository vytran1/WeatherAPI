package com.weatherapi.weatherforecast.full;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.weatherapi.weatherforecast.daily.DailyWeatherDTO;
import com.weatherapi.weatherforecast.hourly.HourlyWeatherDTO;
import com.weatherapi.weatherforecast.realtime.RealtimeWeatherDTO;

public class FullWeatherDTO {
   
	private String location;
	
	@JsonProperty("realtime_weather")
	@JsonInclude(value = JsonInclude.Include.CUSTOM,valueFilter = RealtimeWeatherDTOFieldFilter.class)
	private RealtimeWeatherDTO realtimeWeather = new RealtimeWeatherDTO();
	
	@JsonProperty("hourly_forecast")
	private List<HourlyWeatherDTO> hourlyweathers= new ArrayList<>();
	
	@JsonProperty("daily_forecast")
	private List<DailyWeatherDTO>  dailyweathers = new ArrayList<>();

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public RealtimeWeatherDTO getRealtimeWeather() {
		return realtimeWeather;
	}

	public void setRealtimeWeather(RealtimeWeatherDTO realtimeWeather) {
		this.realtimeWeather = realtimeWeather;
	}

	public List<HourlyWeatherDTO> getHourlyweathers() {
		return hourlyweathers;
	}

	public void setHourlyweathers(List<HourlyWeatherDTO> hourlyweathers) {
		this.hourlyweathers = hourlyweathers;
	}

	public List<DailyWeatherDTO> getDailyweathers() {
		return dailyweathers;
	}

	public void setDailyweathers(List<DailyWeatherDTO> dailyweathers) {
		this.dailyweathers = dailyweathers;
	}

	
	
	
}
