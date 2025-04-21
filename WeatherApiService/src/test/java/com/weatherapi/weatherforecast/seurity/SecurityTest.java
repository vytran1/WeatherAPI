package com.weatherapi.weatherforecast.seurity;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityTest {
		
	
	private static final String GET_ACCESS_TOKEN_ENDPOINT = "/oauth2/token";
	
	
	@Autowired
	MockMvc mockMvc;
	
	
	@Test
	public void testGetAccessTokenFail() throws Exception {
		mockMvc.perform(post(GET_ACCESS_TOKEN_ENDPOINT)
				.param("client_id","abc")
				.param("client_secret","abc")
				.param("grant_type","client_credentials")
				)
		.andDo(print())
		.andExpect(status().isUnauthorized())
		.andExpect(jsonPath("$.error",is("invalid_client")));
	}
	
	@Test
	public void testGetAccessTokenSuccess() throws Exception {
		mockMvc.perform(post(GET_ACCESS_TOKEN_ENDPOINT)
				.param("client_id","lQZhpQE6br0ipC8o3fVX")
				.param("client_secret","E2j2e63gtLetGnGvtKH0xVg66EFhIepWRSfyD8xF")
				.param("grant_type","client_credentials")
				)
		.andDo(print())
		.andExpect(status().isUnauthorized())
		.andExpect(jsonPath("$.error",is("invalid_client")));
	}
	
}
