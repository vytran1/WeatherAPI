package com.weatherapi.weatherforecast.realtime;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.weatherapi.weatherforecast.common.Location;
import com.weatherapi.weatherforecast.common.RealtimeWeather;
import com.weatherapi.weatherforecast.location.LocationNotFoundException;
import com.weatherapi.weatherforecast.location.LocationRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class RealtimeWeatherService {
    
	
	@Autowired
	private RealtimeWeatherRepository realtimeWeatherRepository;
	
	@Autowired
	private LocationRepository localLocationRepository;
	
	
	
	@Cacheable(cacheNames = "realtimeWeatherByIPCache",key = "{#location.countryCode,#location.cityName}")
	public RealtimeWeather getByCountryCodeAndCityName(Location location) throws LocationNotFoundException {
		String countryCode = location.getCountryCode();
		String cityName = location.getCityName();
		System.out.println(countryCode);
		System.out.println(cityName);

		RealtimeWeather realtimeWeatherInDB = realtimeWeatherRepository.findByCountryCodeAndCity(countryCode, cityName);
		
		if(realtimeWeatherInDB == null) {
			throw new LocationNotFoundException("No location with the given country code and city name");
		}else {
			return realtimeWeatherInDB;
		}
	}
	
	
	@Cacheable("realtimeWeatherByCodeCache")
	public RealtimeWeather getByLocationCode(String locationCode) throws LocationNotFoundException {
		RealtimeWeather resultInDB = realtimeWeatherRepository.findByLocationCode(locationCode);
		if(resultInDB == null) {
			throw new LocationNotFoundException("No location exist with the given location code");
		}else {
			return resultInDB;
		}
	}
	
	@CachePut(cacheNames = "realtimeWeatherByCodeCache",key = "#locationCode")
	@CacheEvict(cacheNames = "realtimeWeatherByIPCache", allEntries = true)
	public RealtimeWeather update(String locationCode,RealtimeWeather realtimeWeather) throws LocationNotFoundException {
		Location location = localLocationRepository.findByCode(locationCode);
		if(location == null) {
			throw new LocationNotFoundException("No location exist with the given location code");
		}
		realtimeWeather.setLocation(location);
		realtimeWeather.setLastUpdated(new Date());
		
		if(location.getRealtimeWeather() == null) {
			location.setRealtimeWeather(realtimeWeather);
			Location updatedLocation = localLocationRepository.save(location);
			return updatedLocation.getRealtimeWeather();
		}
		
		return realtimeWeatherRepository.save(realtimeWeather);
	}
	
	
}
