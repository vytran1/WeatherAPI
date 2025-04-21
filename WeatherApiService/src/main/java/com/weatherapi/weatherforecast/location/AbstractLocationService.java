package com.weatherapi.weatherforecast.location;

import org.springframework.cache.annotation.Cacheable;

import com.weatherapi.weatherforecast.common.Location;

public abstract class AbstractLocationService {
	
	protected LocationRepository locationRepository;
	
	
	@Cacheable(cacheNames = "locationCacheByCode",key = "#code")
	public Location get(String code){
		Location location = locationRepository.findByCode(code);
		if(location == null) {
			throw new LocationNotFoundException("Not exist location with given location code");
		}
		return location;
	}

}
