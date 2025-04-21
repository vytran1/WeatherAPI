package com.weatherapi.weatherforecast;

import org.springframework.beans.factory.annotation.Autowired;

import com.weatherapi.weatherforecast.common.Location;
import com.weatherapi.weatherforecast.location.LocationNotFoundException;
import com.weatherapi.weatherforecast.location.LocationRepository;

public abstract class AbstractLocationService {
    
	@Autowired
	private LocationRepository locationRepository;
	
	
	
	public Location get(String code){
		Location location = locationRepository.findByCode(code);
		if(location == null) {
			throw new LocationNotFoundException("Not exist location with given location code");
		}
		return location;
	}
}
