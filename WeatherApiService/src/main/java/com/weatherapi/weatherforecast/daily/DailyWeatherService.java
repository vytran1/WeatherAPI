package com.weatherapi.weatherforecast.daily;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.weatherapi.weatherforecast.common.DailyWeather;
import com.weatherapi.weatherforecast.common.Location;
import com.weatherapi.weatherforecast.location.LocationNotFoundException;
import com.weatherapi.weatherforecast.location.LocationRepository;

@Service
public class DailyWeatherService {
   
	@Autowired
	private LocationRepository locationRepository;
	
	@Autowired
	private DailyWeatherRepository dailyWeatherRepository;
	
	
	@Cacheable(cacheNames = "dailyWeatherByIPCache",key = "{#location.countryCode,#location.cityName}")
	public List<DailyWeather> listByLocationCode(Location location){
		String countryCode = location.getCountryCode();
		String cityName = location.getCityName();
		System.out.println("Country Code: " + countryCode);
		System.out.println("CityName: " + cityName);
		Location locationInDB = locationRepository.findByCountryCodeAndCityName(countryCode, cityName);
		
		if(locationInDB == null) {
			throw new LocationNotFoundException("Not Exist Location with given Location Code");
		}
		
		List<DailyWeather> listDailyWeatherFromDB = dailyWeatherRepository.findByLocationCode(locationInDB.getCode());
		
		return listDailyWeatherFromDB;
	}
	
	@Cacheable(cacheNames = "dailyWeatherByCodeCache",key = "#locationCode")
	public List<DailyWeather> listByLocationCode(String locationCode){
		Location locationInDB = locationRepository.findByCode(locationCode);
		if(locationInDB == null) {
			throw new LocationNotFoundException("Not exist location with given location code");
		}
		
		List<DailyWeather> listDailyWeathers = dailyWeatherRepository.findByLocationCode(locationCode);
		return listDailyWeathers;
	}
	
	
	@CachePut(cacheNames = "dailyWeatherByCodeCache",key = "#locationCode")
	@CacheEvict(cacheNames = "dailyWeatherByIPCache",allEntries = true)
	public List<DailyWeather> updateDailyWeather(String locationCode,List<DailyWeather> dailyWeatherInRequest){
		Location locationInDB = locationRepository.findByCode(locationCode);
		
		if(locationInDB == null) {
			throw new LocationNotFoundException("Not exist location with given location code");
		}
		
		
		for(DailyWeather dailyWeather : dailyWeatherInRequest) {
			dailyWeather.getId().setLocation(locationInDB);
		}
		
		List<DailyWeather> listDailyWeatherInDB = locationInDB.getDailyweathers();
		List<DailyWeather> listDailyWeatherNeedToDeleted = new ArrayList<>();
		
		for(DailyWeather item : listDailyWeatherInDB) {
			if(!dailyWeatherInRequest.contains(item)) {
				listDailyWeatherNeedToDeleted.add(item.copy());
			}
		}
		
		
		for(DailyWeather item : listDailyWeatherNeedToDeleted) {
			listDailyWeatherInDB.remove(item);
		}
		
		
		return (List<DailyWeather>) dailyWeatherRepository.saveAll(dailyWeatherInRequest);
	}
}
