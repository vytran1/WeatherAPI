package com.weatherapi.weatherforecast.full;

import com.weatherapi.weatherforecast.realtime.RealtimeWeatherDTO;

public class RealtimeWeatherDTOFieldFilter {
   
	
	public boolean equals(Object object) {
		if(object instanceof RealtimeWeatherDTO) {
			RealtimeWeatherDTO dto  = (RealtimeWeatherDTO) object;
			return dto.getStatus() == null;
		}
		return false;
	}
}
