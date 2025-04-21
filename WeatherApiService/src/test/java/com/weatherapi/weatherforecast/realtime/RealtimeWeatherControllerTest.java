package com.weatherapi.weatherforecast.realtime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weatherapi.weatherforecast.GeoLocationException;
import com.weatherapi.weatherforecast.GeolocationService;
import com.weatherapi.weatherforecast.common.Location;
import com.weatherapi.weatherforecast.common.RealtimeWeather;
import com.weatherapi.weatherforecast.location.LocationNotFoundException;
import com.weatherapi.weatherforecast.location.LocationService;

@WebMvcTest(controllers = RealtimeWeatherController.class)
public class RealtimeWeatherControllerTest {
   
	private static String END_POINT_PATH = "/v1/realtime";

	
	@Autowired
    MockMvc mockMvc;
	
	@Autowired
	ObjectMapper objectMapper;
	
	@MockBean
	RealtimeWeatherService realtimeWeatherService;
	
	@MockBean
	GeolocationService locationService;
	
	
	@Test
	public void testGetShouldReturnBadRequest() throws Exception {
		Mockito.when(locationService.getLocation(Mockito.anyString())).thenThrow(GeoLocationException.class);
		mockMvc.perform(get(END_POINT_PATH)).andExpect(status().isBadRequest()).andDo(print());
	}
	
	@Test
	public void testGetShouldReturnNotFound() throws Exception {
		Location location = new Location();
		Mockito.when(locationService.getLocation(Mockito.anyString())).thenReturn(location);
		Mockito.when(realtimeWeatherService.getByCountryCodeAndCityName(location)).thenThrow(LocationNotFoundException.class);
		mockMvc.perform(get(END_POINT_PATH)).andExpect(status().isNotFound()).andDo(print());
	}
	
	
	@Test
	public void testGetShouldReturnOK() throws Exception {
		Location location = new Location();
		location.setCode("SFCA_USA");
		location.setCityName("San Franciso");
		location.setRegionName("California");
		location.setCountryName("United States of America");
		location.setCountryCode("US");

		RealtimeWeather realtimeWeather = new RealtimeWeather();
		realtimeWeather.setTemperature(12);
		realtimeWeather.setHumidity(32);
		realtimeWeather.setLastUpdated(new Date());
		realtimeWeather.setStatus("Windy");
		realtimeWeather.setPrecipitation(88);
		realtimeWeather.setWindSpeed(55);
		realtimeWeather.setLocation(location);
		location.setRealtimeWeather(realtimeWeather);

		Mockito.when(locationService.getLocation(Mockito.anyString())).thenReturn(location);
		Mockito.when(realtimeWeatherService.getByCountryCodeAndCityName(location)).thenReturn(realtimeWeather);
		
		String expectedLocation = location.getCityName() + ", " + location.getRegionName() + ", " + location.getCountryName();
		
		mockMvc.perform(get(END_POINT_PATH)).andExpect(status().isOk())
				.andExpect(content().contentType("application/hal+json")).andDo(print());
	}
	
	
	
	@Test
	public void testGetRealtimeLocationWithNotFound() throws Exception {
		String locationCode = "ABC";
		String requestURL = END_POINT_PATH + "/" + locationCode;
		Mockito.when(realtimeWeatherService.getByLocationCode(locationCode)).thenThrow(LocationNotFoundException.class);
		mockMvc.perform(get(requestURL)).andExpect(status().isNotFound()).andDo(print());
	}
	
	
	@Test
	public void testGetRealtimeLocationWithOK() throws Exception {
		String locationCode = "NYC_USA";
		Location location = new Location();
		location.setCode(locationCode);
		RealtimeWeather realtimeWeather = new RealtimeWeather();
		realtimeWeather.setTemperature(12);
		realtimeWeather.setHumidity(32);
		realtimeWeather.setLastUpdated(new Date());
		realtimeWeather.setStatus("Windy");
		realtimeWeather.setPrecipitation(88);
		realtimeWeather.setWindSpeed(55);
		realtimeWeather.setLocation(location);
		location.setRealtimeWeather(realtimeWeather);
		String requestURL = END_POINT_PATH + "/" + locationCode;
		Mockito.when(realtimeWeatherService.getByLocationCode(locationCode)).thenReturn(realtimeWeather);
		mockMvc.perform(get(requestURL)).andExpect(status().isOk()).andDo(print());
	}
	
	
	@Test
	public void testUpdateWithResponseBadRequest() throws Exception {
		String locationCode = "NYC_USA";
		String requestURL = END_POINT_PATH + "/" + locationCode;
		RealtimeWeather realtimeWeather = new RealtimeWeather();
		realtimeWeather.setTemperature(120);
		realtimeWeather.setHumidity(132);
		realtimeWeather.setLastUpdated(new Date());
		realtimeWeather.setStatus("W");
		realtimeWeather.setPrecipitation(188);
		realtimeWeather.setWindSpeed(550);
		String requestBody = objectMapper.writeValueAsString(realtimeWeather);
		mockMvc.perform(put(requestURL).contentType("application/json").content(requestBody))
		       .andExpect(status().isBadRequest())
		       .andDo(print());
	}
	

	@Test
	public void testUpdateWithResponseNotFound() throws Exception {
		String locationCode = "NYC_USA";
		String requestURL = END_POINT_PATH + "/" + locationCode;
		
		
		Location location = new Location();
		location.setCode(locationCode);
		location.setCityName("San Franciso");
		location.setRegionName("California");
		location.setCountryName("United States of America");
		location.setCountryCode("US");
		
		
		RealtimeWeather realtimeWeather = new RealtimeWeather();
		realtimeWeather.setTemperature(12);
		realtimeWeather.setHumidity(32);
		realtimeWeather.setLastUpdated(new Date());
		realtimeWeather.setStatus("Windy");
		realtimeWeather.setPrecipitation(88);
		realtimeWeather.setWindSpeed(5);
		realtimeWeather.setLastUpdated(new Date());
		
		
		realtimeWeather.setLocation(location);
		location.setRealtimeWeather(realtimeWeather);
		
		
		String requestBody = objectMapper.writeValueAsString(realtimeWeather);
		Mockito.when(realtimeWeatherService.update(locationCode, realtimeWeather)).thenReturn(realtimeWeather);
		mockMvc.perform(put(requestURL).contentType("application/json").content(requestBody))
		       .andExpect(status().isOk())
		       .andDo(print());
	}
	
}
