package com.weatherapi.weatherforecast.full;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import com.weatherapi.weatherforecast.hourly.HourlyWeatherController;
import com.weatherapi.weatherforecast.location.LocationNotFoundException;

@WebMvcTest(controllers = FullWeatherController.class)
public class FullWeatherApiControllerTest {
    
	private static final String END_POINT_PATH = "/v1/full";
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@MockBean
	private GeolocationService geolocationService;
	
	@MockBean
	private FullWeatherService fullWeatherService;
	
	@Test
	public void testGetFullWeatherByIPAddressWith400Response() throws Exception {
		GeoLocationException ex = new GeoLocationException("Geolocation error");
		Mockito.when(geolocationService.getLocation(Mockito.anyString())).thenThrow(ex);
		
		mockMvc.perform(get(END_POINT_PATH)).andExpect(status().isBadRequest()).andDo(print());
	}
	
	
	@Test
	public void testGetFullWeatherByIPAddressWith404Response() throws Exception {
		GeoLocationException ex = new GeoLocationException("Geolocation error");
		Location location = new Location();
		Mockito.when(geolocationService.getLocation(Mockito.anyString())).thenReturn(location);
		Mockito.when(fullWeatherService.getByLocation(location)).thenThrow(LocationNotFoundException.class);
		
		mockMvc.perform(get(END_POINT_PATH)).andExpect(status().isNotFound()).andDo(print());
	}
}
