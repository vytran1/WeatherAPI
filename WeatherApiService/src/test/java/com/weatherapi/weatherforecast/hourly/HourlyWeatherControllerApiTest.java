package com.weatherapi.weatherforecast.hourly;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weatherapi.weatherforecast.GeoLocationException;
import com.weatherapi.weatherforecast.GeolocationService;
import com.weatherapi.weatherforecast.common.HourlyWeather;
import com.weatherapi.weatherforecast.common.Location;

@WebMvcTest(controllers = HourlyWeatherController.class)
public class HourlyWeatherControllerApiTest {
	private static final String X_CURRENT_HOUR = "X-Current-Hour";
    private static final String END_POINT_PATH = "/v1/hourly";
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	
	@MockBean
	private HourlyWeatherService hourlyWeatherService;
	
	@MockBean
	private GeolocationService geolocationService;
	
	
	
	@Test
	public void testGetByIPWithBadRequest() throws Exception {
		mockMvc.perform(get(END_POINT_PATH)).andExpect(status().isBadRequest()).andDo(print());
	}
	
	@Test
	public void testGetByIPWithBadRequest2() throws Exception {
		
		Mockito.when(geolocationService.getLocation(Mockito.anyString())).thenThrow(GeoLocationException.class);
		mockMvc.perform(get(END_POINT_PATH).header("X-Current-Hour","9")).andExpect(status().isBadRequest()).andDo(print());
	}
	
	@Test
	public void testGetByIPWithNoContent() throws Exception {
		int currentHour = 9;
		Location location = new Location();
		location.setCode("DELHI_IN");
		
		Mockito.when(geolocationService.getLocation(Mockito.anyString())).thenReturn(location);
		Mockito.when(hourlyWeatherService.getByLocatio(location,currentHour)).thenReturn(new ArrayList<>());
		mockMvc.perform(get(END_POINT_PATH).header("X-Current-Hour",String.valueOf(currentHour))).andExpect(status().isNoContent()).andDo(print());
	}
	
	@Test
	public void testGetByIPWithOk() throws Exception {
		int currentHour = 9;
		Location location = new Location();
		location.setCode("MBMH_IN");
		location.setCityName("Mumbai");
		location.setRegionName("Maharashtra");
		location.setCountryCode("IN");
		location.setCountryName("India");
		location.setEnabled(true);
		
		
        List<HourlyWeather> hourlyWeathers = location.getHourlyweathers();
		
		HourlyWeather hourlyWeather = new HourlyWeather()
				.id(location,8)
				.temperature(20)
				.precipitation(20)
				.status("Windy");
		
		HourlyWeather hourlyWeather2 = new HourlyWeather()
				.location(location).hourOfDay(9)
				.temperature(20)
				.precipitation(20)
				.status("Windy");
		hourlyWeathers.add(hourlyWeather);
		hourlyWeathers.add(hourlyWeather2);
		
		
		Mockito.when(geolocationService.getLocation(Mockito.anyString())).thenReturn(location);
		Mockito.when(hourlyWeatherService.getByLocatio(location,currentHour)).thenReturn(hourlyWeathers);
		mockMvc.perform(get(END_POINT_PATH).header("X-Current-Hour",String.valueOf(currentHour))).andExpect(status().isOk()).andDo(print());
	}
	
	@Test
	public void testUpdateHourlyWithBadRequest() throws Exception {
		String requestURL = END_POINT_PATH + "/DELHI_IN";
		
		List<HourlyWeatherDTO> listDTO = Collections.emptyList();
		
		String requestBody = objectMapper.writeValueAsString(listDTO);
		
		mockMvc.perform(put(requestURL).contentType(MediaType.APPLICATION_JSON).content(requestBody))
		       .andExpect(status().isBadRequest())
		       .andDo(print());
	}
	
	
	@Test
	public void testUpdateHourlyWithBadRequestDueToInvalidData() throws Exception {
		String requestURL = END_POINT_PATH + "/DELHI_IN";
		
		List<HourlyWeatherDTO> listDTO = new ArrayList<>();
		
		HourlyWeatherDTO dto1 = new HourlyWeatherDTO()
				.hourOfDay(10)
				.temperature(50)
				.precipitation(200)
				.status("Windy");
		
		HourlyWeatherDTO dto2 = new HourlyWeatherDTO()
			    .hourOfDay(9)
				.temperature(20)
				.precipitation(20)
				.status("Windy");
		
		listDTO.add(dto2);
		listDTO.add(dto1);
		
		
		String requestBody = objectMapper.writeValueAsString(listDTO);
		
		mockMvc.perform(put(requestURL).contentType(MediaType.APPLICATION_JSON).content(requestBody))
		       .andExpect(status().isBadRequest())
		       .andDo(print());
	}
	
	
	@Test
	public void testUpdateHourlyWithOk() throws Exception {
		String requestURL = END_POINT_PATH + "/DELHI_IN";
		String locationCode = "DELHI_IN";
		List<HourlyWeatherDTO> listDTO = new ArrayList<>();
		
		HourlyWeatherDTO dto1 = new HourlyWeatherDTO()
				.hourOfDay(10)
				.temperature(13)
				.precipitation(70)
				.status("Windy");
		
		HourlyWeatherDTO dto2 = new HourlyWeatherDTO()
			    .hourOfDay(10)
				.temperature(20)
				.precipitation(20)
				.status("Windy");
		
		listDTO.add(dto2);
		listDTO.add(dto1);
		
		
		
		Location location = new Location();
		location.setCode("DELHI_IN");
		location.setCityName("Mumbai");
		location.setRegionName("Maharashtra");
		location.setCountryCode("IN");
		location.setCountryName("India");
		location.setEnabled(true);
		
		
        List<HourlyWeather> hourlyWeathers = location.getHourlyweathers();
		
		HourlyWeather hourlyWeather = new HourlyWeather()
				.id(location,10)
				.temperature(13)
				.precipitation(70)
				.status("Windy");
		
		HourlyWeather hourlyWeather2 = new HourlyWeather()
				.location(location).hourOfDay(10)
				.temperature(20)
				.precipitation(20)
				.status("Windy");
		hourlyWeathers.add(hourlyWeather);
		hourlyWeathers.add(hourlyWeather2);
		
		Mockito.when(hourlyWeatherService.updateHourlyWeather(Mockito.eq(locationCode),Mockito.anyList())).thenReturn(hourlyWeathers);
		
		String requestBody = objectMapper.writeValueAsString(listDTO);
		
		mockMvc.perform(put(requestURL).contentType(MediaType.APPLICATION_JSON).content(requestBody))
		       .andExpect(status().isOk())
		       .andDo(print());
	}
	
}
