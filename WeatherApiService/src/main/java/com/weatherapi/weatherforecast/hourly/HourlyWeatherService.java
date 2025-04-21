package com.weatherapi.weatherforecast.hourly;

import java.util.ArrayList;
import java.util.List;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.weatherapi.weatherforecast.common.HourlyWeather;
import com.weatherapi.weatherforecast.common.Location;
import com.weatherapi.weatherforecast.location.LocationNotFoundException;
import com.weatherapi.weatherforecast.location.LocationRepository;

@Service
public class HourlyWeatherService {
    
	@Autowired
	private HourlyWeatherRepository hourlyWeatherRepository;
	
	@Autowired
	private LocationRepository locationRepository;
	
	
	
	@Cacheable(cacheNames = "hourlyWeatherByIPCache",key = "{#location.countryCode,#location.cityName,#currentHour}")
	public List<HourlyWeather> getByLocatio(Location location,Integer currentHour) throws LocationNotFoundException{
		String countryCode = location.getCountryCode();
		String cityName = location.getCityName();
		Location locationInDB = locationRepository.findByCountryCodeAndCityName(countryCode, cityName);
		if(locationInDB == null ) {
			throw new LocationNotFoundException("Not exist location with given information");
		}
		return hourlyWeatherRepository.findByLocationCode(locationInDB.getCode(),currentHour);
		
	}
	
	@Cacheable(cacheNames = "hourlyWeatherByCodeCache",key = "{#locationCode,#currentHour}")
	public List<HourlyWeather> getByLocationCode(String locationCode,Integer currentHour) throws LocationNotFoundException{
		Location locationInDB = locationRepository.findByCode(locationCode);
		if(locationInDB == null) {
			throw new LocationNotFoundException("Not Exist Location with given location Code");
		}
		
		return hourlyWeatherRepository.findByLocationCode(locationInDB.getCode(),currentHour);
	}
	
	@CacheEvict(cacheNames = {"hourlyWeatherByIPCache","hourlyWeatherByCodeCache"},allEntries = true)
	public List<HourlyWeather> updateHourlyWeather(String locationCode,List<HourlyWeather> list) throws BadRequestException, LocationNotFoundException {
		Location location  = locationRepository.findByCode(locationCode);
		if(location == null) {
			throw new LocationNotFoundException("Not exist location with given location code");
		}
		
		for(HourlyWeather item : list) {
			item.getId().setLocation(location);
		}
		
		List<HourlyWeather> listHourlyWeatherInDB = location.getHourlyweathers();
		List<HourlyWeather> listHourlyWeatherToBeRemoved = new ArrayList<>();
		for(HourlyWeather item : listHourlyWeatherInDB) {
			if(!list.contains(item)) {
				listHourlyWeatherToBeRemoved.add(item.copy());
			}
		}
		
		for(HourlyWeather item : listHourlyWeatherToBeRemoved) {
			listHourlyWeatherInDB.remove(item);
		}
		
		
		return (List<HourlyWeather>) hourlyWeatherRepository.saveAll(list);
	}
}
