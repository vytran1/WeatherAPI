package com.weatherapi.weatherforecast;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ip2location.IP2Location;
import com.ip2location.IPResult;
import com.weatherapi.weatherforecast.common.Location;

@Service
public class GeolocationService {
	   private static final Logger logger = LoggerFactory.getLogger(GeolocationService.class);
	   private String dbPath = "ip2locdb/IP2LOCATION-LITE-DB3.BIN";
	   private IP2Location ip2Location = new IP2Location();
	   
	   public GeolocationService() {
		   try {
			   ip2Location.Open(dbPath);
		   }catch(Exception ex) {
			   logger.error(ex.getMessage(),ex);
			   
		   }
	   }
	   
	   
	   public Location getLocation(String ipAddress) throws GeoLocationException {
		   try {
			IPResult ipResult = ip2Location.IPQuery(ipAddress);
			if(!(ipResult.getStatus().equals("OK"))) {
				throw new GeoLocationException("Geolocation fail with status " + ipResult.getStatus());
			}else {
				Location locationResult = new Location(ipResult.getCity(),ipResult.getRegion(),ipResult.getCountryLong(),ipResult.getCountryShort());
				System.out.println(locationResult);
				return new Location(ipResult.getCity(),ipResult.getRegion(),ipResult.getCountryLong(),ipResult.getCountryShort());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new GeoLocationException("Error Query IP database",e);
		}
		   
	   }
	   
}
