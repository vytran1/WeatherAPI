package com.weatherapi.weatherforecast.base;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import com.weatherapi.weatherforecast.SecurityConfigForControllerTest;

@WebMvcTest(controllers = MainController.class)
//@Import(SecurityConfigForControllerTest.class)
public class MainControllerTest {
   private static final String baseURL = "/";
   
   @Autowired
   private MockMvc mockMvc;
   
   
   @Test
   public void testBaseURI() throws Exception {
	   mockMvc.perform(get(baseURL)).andExpect(status().isOk()).andExpect(content().contentType("application/json")).andDo(print());
   }
   
}
