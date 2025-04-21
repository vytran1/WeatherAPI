package com.weatherapi.clientmanager.admin.location;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.weatherapi.weatherforecast.common.Location;

@Service
public class LocationService {
	
	
	@Autowired
	private LocationRepository locationRepository;
	
	
	public List<Location> searchAutoComplete(String keyword){
		List<Location> result = locationRepository.search(keyword);
		
		return result;
	}
	
	public Location get(String code) throws LocationNotFoundException {
		Location location = locationRepository.findByCodeEnabledUntrashed(code);
		if (location == null) {
			throw new LocationNotFoundException("No location found with the given code: " + code);
		}
		
		return location;
	}
	
}
