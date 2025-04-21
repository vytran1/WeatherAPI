package com.weatherapi.weatherforecast.full;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import com.weatherapi.weatherforecast.common.DailyWeather;
import com.weatherapi.weatherforecast.common.HourlyWeather;
import com.weatherapi.weatherforecast.common.Location;
import com.weatherapi.weatherforecast.common.RealtimeWeather;
import com.weatherapi.weatherforecast.location.AbstractLocationService;
import com.weatherapi.weatherforecast.location.LocationNotFoundException;
import com.weatherapi.weatherforecast.location.LocationRepository;

@Service
public class FullWeatherService extends AbstractLocationService {
    
	
	
	
	
	
	
	public FullWeatherService(LocationRepository locationRepository) {
		super();
		// TODO Auto-generated constructor stub
		this.locationRepository = locationRepository;
		
	}


	@Cacheable(cacheNames = "fullWeatherByIPCache", key = "{#locationFromIPAddress.cityName,#locationFromIPAddress.countryCode}")
	public Location getByLocation(Location locationFromIPAddress) {
        String cityName = locationFromIPAddress.getCityName();
        String countryCode = locationFromIPAddress.getCountryCode();
        
        
        Location locationFromDB = locationRepository.findByCountryCodeAndCityName(countryCode, cityName);
        
        
        if(locationFromDB == null) {
        	throw new LocationNotFoundException("Not Exist Location with given IP Address");
        }
        
        
        return locationFromDB;
	}
	
	
	public Location getByLocationCode(String code) {
		Location locationInDB = locationRepository.findByCode(code);
		if(locationInDB == null) {
			throw new LocationNotFoundException("Not Exist Location with given Location Code: " + code);
		}
		return locationInDB;
	}
	
	
	
	@Caching(
			
			put = {@CachePut(cacheNames = "locationCacheByCode",key = "#code")},
			evict = {
					@CacheEvict(cacheNames = {"fullWeatherByIPCache","realtimeWeatherByIPCache"
											 ,"hourlyWeatherByIPCache","dailyWeatherByIPCache"}, allEntries = true),
					
					
					@CacheEvict(cacheNames = {"realtimeWeatherByCodeCache","hourlyWeatherByCodeCache",
											  "dailyWeatherByCodeCache"},
								key = "#code"),
					
			}
			
			)
	public Location updateFullWeatherData(String code,Location locationInRequest) {
		Location locationInDB = locationRepository.findByCode(code);
		
		if(locationInDB == null) {
			throw new LocationNotFoundException("Not Exist Location with given Location Code: " + code);
		}
		
		RealtimeWeather realtimeWeatherInRequest = locationInRequest.getRealtimeWeather();
		realtimeWeatherInRequest.setLocation(locationInDB);
		realtimeWeatherInRequest.setLastUpdated(new Date());
		
		if(locationInDB.getRealtimeWeather() == null) {
			locationInDB.setRealtimeWeather(realtimeWeatherInRequest);
			locationRepository.save(locationInDB);
		}
		
		List<DailyWeather> listDailyWeathersInRequest = locationInRequest.getDailyweathers();
		listDailyWeathersInRequest.forEach((item) -> item.getId().setLocation(locationInDB));
		
		List<HourlyWeather> listHourlyWeatherInRequest = locationInRequest.getHourlyweathers();
		listHourlyWeatherInRequest.forEach((item) -> item.getId().setLocation(locationInDB));
		
		locationInRequest.copyAllFieldsFromLocation(locationInDB);
		
		return locationRepository.save(locationInRequest);
	}
	
}
