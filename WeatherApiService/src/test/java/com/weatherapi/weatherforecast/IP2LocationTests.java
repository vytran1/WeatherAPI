package com.weatherapi.weatherforecast;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.ip2location.IP2Location;
import com.ip2location.IPResult;

public class IP2LocationTests {
   private String dbPath = "ip2locdb/IP2LOCATION-LITE-DB3.BIN";
   
   @Test
   public void testInValid() throws IOException {
	   IP2Location ipLocator = new IP2Location();
	   ipLocator.Open(dbPath);
	   
	   
	   String ipAddress = "abc";
	   IPResult ipResult = ipLocator.IPQuery(ipAddress);
	   
	   assertThat(ipResult.getStatus()).isEqualTo("INVALID_IP_ADDRESS");
	   System.out.println(ipResult);
   }
   
   
   @Test
   public void testValid() throws IOException {
	   IP2Location ipLocator = new IP2Location();
	   ipLocator.Open(dbPath);
	   
	   
	   String ipAddress = "118.69.140.108";
	   IPResult ipResult = ipLocator.IPQuery(ipAddress);
	   
	   assertThat(ipResult.getStatus()).isEqualTo("OK");
	   assertThat(ipResult.getCity()).isEqualTo("Bien Hoa");
	   System.out.println(ipResult);
   }
}
