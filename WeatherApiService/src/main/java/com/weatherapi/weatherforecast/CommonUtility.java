package com.weatherapi.weatherforecast;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletRequest;

public class CommonUtility {
	   private static final Logger logger = LoggerFactory.getLogger(CommonUtility.class);
       
	   public static String getIpAddress(HttpServletRequest request) {
		   String ip = request.getHeader("X-FORWARED-FOR");
		   if(ip == null || ip.isEmpty()) {
			   ip = request.getRemoteAddr();
		   }
		   logger.info("Client's IP Address: " + ip);
		   return ip;
		   
	   }
}
