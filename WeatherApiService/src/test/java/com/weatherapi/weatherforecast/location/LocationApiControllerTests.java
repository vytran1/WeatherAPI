package com.weatherapi.weatherforecast.location;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysql.cj.x.protobuf.MysqlxCrud.Collection;
import com.weatherapi.weatherforecast.common.Location;

@WebMvcTest(controllers = LocationApiController.class)
public class LocationApiControllerTests {
	
	private static String END_POINT_PATH = "/v1/locations";
	
    //Use to send request to controller
	@Autowired
    MockMvc mockMvc;
	
	@Autowired
	ObjectMapper objectMapper;
	
	@MockBean
	LocationService locationService;
	
	@Test
	public void testAddWithResponse400Status() throws Exception {
		Location location = new Location();
		String requestBody = objectMapper.writeValueAsString(location);
		mockMvc.perform(post(END_POINT_PATH).contentType("application/json").content(requestBody))
		       .andExpect(status().isBadRequest())
		       .andDo(print());
	}
	
	
	@Test
	public void testAddWithResponse201Status() throws Exception {
		Location newLocation = new Location();
		newLocation.setCode("NYC_USA");
		newLocation.setCityName("New York City");
		newLocation.setRegionName("New York");
		newLocation.setCountryCode("us");
		newLocation.setCountryName("United State of America");
		newLocation.setEnabled(true);
		
		Mockito.when(locationService.add(newLocation)).thenReturn(newLocation);
		
		String requestBody = objectMapper.writeValueAsString(newLocation);
		
		mockMvc.perform(post(END_POINT_PATH).contentType("application/json").content(requestBody))
	       .andExpect(status().isCreated())
	       .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content().contentType("application/json"))
	       .andExpect(jsonPath("$.code",is("NYC_USA")))
	       .andExpect(header().string("Location","/v1/locations/NYC_USA"))
	       .andDo(print());
	}
	
	@Test
	public void testListLocationsWithResponseNoContent() throws Exception {
		Mockito.when(locationService.list()).thenReturn(Collections.EMPTY_LIST);
		mockMvc.perform(get(END_POINT_PATH))
		       .andExpect(status().isNoContent())
		       .andDo(print());
	}
	
	@Test
	public void testListLocationWithResponseOk() throws Exception {
		Location newLocation1 = new Location();
		newLocation1.setCode("NYC_USA");
		newLocation1.setCityName("New York City");
		newLocation1.setRegionName("New York");
		newLocation1.setCountryCode("us");
		newLocation1.setCountryName("United State of America");
		newLocation1.setEnabled(true);
		
		Location newLocation2 = new Location();
		newLocation2.setCode("LACA_USA");
		newLocation2.setCityName("Los Angles City");
		newLocation2.setRegionName("California");
		newLocation2.setCountryCode("us");
		newLocation2.setCountryName("United State of America");
		newLocation2.setEnabled(true);
		
		Location newLocation3 = new Location();
		newLocation3.setCode("LACA_USA");
		newLocation3.setCityName("Los Angles City");
		newLocation3.setRegionName("California");
		newLocation3.setCountryCode("us");
		newLocation3.setCountryName("United State of America");
		newLocation3.setEnabled(true);
		newLocation3.setTrashed(true);
		
		Mockito.when(locationService.list()).thenReturn(List.of(newLocation1,newLocation2,newLocation3));
		mockMvc.perform(get(END_POINT_PATH))
	       .andExpect(status().isOk())
	       .andExpect(header().string("Expires",not("0")))
	       .andDo(print());
	}
    
	@Test
	public void testGetByCodeWithResponseNotAllowed() throws Exception {
		String requestUrl = END_POINT_PATH + "/ABCDEF";
		mockMvc.perform(post(requestUrl))
		.andExpect(status().isMethodNotAllowed())
		.andDo(print());
	}
	
	@Test
	public void testGetByCodeWithResponseNotFound() throws Exception {
		String requestUrl = END_POINT_PATH + "/ABCDEF";
		Mockito.when(locationService.get(requestUrl)).thenReturn(null);
		mockMvc.perform(get(requestUrl))
		.andExpect(status().isNotFound())
		.andDo(print());
	}
	
	@Test
	public void testGetByCodeWithResponseOk() throws Exception {
		String code = "NYC_USA";
		Location newLocation = new Location();
		newLocation.setCode("NYC_USA");
		newLocation.setCityName("New York City");
		newLocation.setRegionName("New York");
		newLocation.setCountryCode("us");
		newLocation.setCountryName("United State of America");
		newLocation.setEnabled(true);
		String requestUrl = END_POINT_PATH + "/" + code;
		Mockito.when(locationService.get(code)).thenReturn(newLocation);
		mockMvc.perform(get(requestUrl))
		.andExpect(status().isOk())
		.andDo(print());
	}
	
	@Test
	public void testUpdateLocationWithResponseNotFound() throws Exception {
		Location newLocation = new Location();
		newLocation.setCode("ABCDEF");
		newLocation.setCityName("New York City");
		newLocation.setRegionName("New York");
		newLocation.setCountryCode("us");
		newLocation.setCountryName("United State of America");
		newLocation.setEnabled(true);
		Mockito.when(locationService.update(newLocation)).thenThrow(new LocationNotFoundException("Not Exist Location with code: " + newLocation.getCode()));
		String requestBody = objectMapper.writeValueAsString(newLocation);
		mockMvc.perform(put(END_POINT_PATH).contentType("application/json").content(requestBody))
		       .andExpect(status().isNotFound())
		       .andDo(print());		   
	}
	
	@Test
	public void testUpdateLocationWithResponseBadRequest() throws Exception {
		Location newLocation = new Location();
		//newLocation.setCode("ABCDEF");
		newLocation.setCityName("New York City");
		newLocation.setRegionName("New York");
		newLocation.setCountryCode("us");
		newLocation.setCountryName("United State of America");
		newLocation.setEnabled(true);
		//Mockito.when(locationService.update(newLocation)).thenReturn(newLocation);
		String requestBody = objectMapper.writeValueAsString(newLocation);
		mockMvc.perform(put(END_POINT_PATH).contentType("application/json").content(requestBody))
		       .andExpect(status().isBadRequest())
		       .andDo(print());		   
	}
	
	@Test
	public void testUpdateLocationWithResponseOk() throws Exception {
		Location newLocation = new Location();
		newLocation.setCode("ABCDEF");
		newLocation.setCityName("New York City");
		newLocation.setRegionName("New York");
		newLocation.setCountryCode("us");
		newLocation.setCountryName("United State of America");
		newLocation.setEnabled(true);
		Mockito.when(locationService.update(newLocation)).thenReturn(newLocation);
		String requestBody = objectMapper.writeValueAsString(newLocation);
		mockMvc.perform(put(END_POINT_PATH).contentType("application/json").content(requestBody))
		       .andExpect(status().isOk())
		       .andDo(print());		   
	}
	
	
	@Test
	public void testDeleteLocationWithResponseNotFound() throws Exception {
		String code = "DELHI_IN";
		String requestUrl = END_POINT_PATH + "/" + code;
		//Mockito.when(locationService.deleteLocationByCode(code)).thenThrow(new LocationNotFoundException("Not Exist Location with code: " + newLocation.getCode()));
		Mockito.doThrow(LocationNotFoundException.class).when(locationService).deleteLocationByCode(code);;
		//String requestBody = objectMapper.writeValueAsString(newLocation);
		mockMvc.perform(delete(requestUrl))
		       .andExpect(status().isNotFound())
		       .andDo(print());		
	}
	
	
	@Test
	public void testDeleteLocationWithResponseNoContent() throws Exception {
		String code = "DELHI_IN";
		String requestUrl = END_POINT_PATH + "/" + code;
		//Mockito.when(locationService.deleteLocationByCode(code)).thenThrow(new LocationNotFoundException("Not Exist Location with code: " + newLocation.getCode()));
		Mockito.doNothing().when(locationService).deleteLocationByCode(code);
		//String requestBody = objectMapper.writeValueAsString(newLocation);
		mockMvc.perform(delete(requestUrl))
		       .andExpect(status().isNoContent())
		       .andDo(print());		
	}
	
	@Test
	public void testValidateRequestBody() throws Exception {
		Location newLocation = new Location();
		String requestBody = objectMapper.writeValueAsString(newLocation);
		
		mockMvc.perform(post(END_POINT_PATH).contentType("application/json").content(requestBody))
	       .andExpect(status().isBadRequest())
	       .andDo(print());
	}
	
	
	@Test
	public void testListLocationsByPageWithResponseBadRequest() throws Exception {
		int pageNum = 0;
		int pageSize = 21;
		String sortField = "code";
		Mockito.when(locationService.listByPage(pageNum, pageSize, sortField)).thenReturn(Page.empty());
		String requestURL = END_POINT_PATH + "?pageNum=" + pageNum + "&pageSize=" + pageSize + "&sortField=" + sortField; 
		mockMvc.perform(get(requestURL))
		       .andExpect(status().isBadRequest())
		       .andDo(print());
	}
	
}
