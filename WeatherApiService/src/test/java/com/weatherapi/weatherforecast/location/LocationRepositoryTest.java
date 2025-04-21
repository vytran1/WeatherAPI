package com.weatherapi.weatherforecast.location;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;

import com.weatherapi.weatherforecast.common.DailyWeather;
import com.weatherapi.weatherforecast.common.HourlyWeather;
import com.weatherapi.weatherforecast.common.Location;
import com.weatherapi.weatherforecast.common.RealtimeWeather;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class LocationRepositoryTest {
    
	
	@Autowired
	private LocationRepository locationRepository;
	
//	@Test
//	public void testAddSuccess() {
//		Location newLocation = new Location();
//		newLocation.setCode("MBMH_IN");
//		newLocation.setCityName("Mumbai");
//		newLocation.setRegionName("Maharashtra");
//		newLocation.setCountryCode("IN");
//		newLocation.setCountryName("India");
//		newLocation.setEnabled(true);
//		Location savedLocation =locationRepository.save(newLocation);
//		assertThat(savedLocation).isNotNull();
//	}
//	
//	@Test
//@Disabled
//	public void testListSuccess() {
//	    List<Location> listLocations = locationRepository.findUntrashed();
//	    assertThat(listLocations).isNotEmpty();
//	    
//	    listLocations.forEach((location) -> System.out.println(location.getCode()));
//	}
//	
//	@Test
//	public void testFindByCodeWithNotFound() {
//		String code = "ABC";
//		Location location = locationRepository.findByCode(code);
//		assertThat(location).isNull();
//	}
//	
//	@Test
//	public void testFindByCodeWithHasExist() {
//		String code = "DELHI_IN";
//		Location location = locationRepository.findByCode(code);
//		assertThat(location).isNotNull();
//	}
//	
//	@Test
//	public void testTrashByCode() {
//		String code = "DELHI_IN";
//		locationRepository.trashByCode(code);
//		
//		Location locationInDB = locationRepository.findByCode(code);
//		assertThat(locationInDB).isNull();
//	}
//	
//	
//	@Test
//	public void testAddRealtimeWeatherData() {
//		String code = "DELHI_IN";
//		
//		Location location = locationRepository.findByCode(code);
//		RealtimeWeather realtimeWeather = location.getRealtimeWeather();
//		if(realtimeWeather == null) {
//			realtimeWeather = new RealtimeWeather();
//			realtimeWeather.setLocation(location);
//			location.setRealtimeWeather(realtimeWeather);
//		}
//		realtimeWeather.setTemperature(10);
//		realtimeWeather.setHumidity(60);
//		realtimeWeather.setPrecipitation(70);
//		realtimeWeather.setStatus("Sunny");
//		realtimeWeather.setWindSpeed(10);
//		realtimeWeather.setLastUpdated(new Date());
//		Location savedLocation = locationRepository.save(location);
//		RealtimeWeather realtimeWeather2 = savedLocation.getRealtimeWeather();
//		assertThat(realtimeWeather2.getLocationCode()).isEqualTo(code);
//	}
//	
//	@Test
//	public void testAddHourlyWeather() {
//		Location location = locationRepository.findByCode("MBMH_IN");
//		
//		List<HourlyWeather> hourlyWeathers = location.getHourlyweathers();
//		
//		HourlyWeather hourlyWeather = new HourlyWeather()
//				.id(location,8)
//				.temperature(20)
//				.precipitation(20)
//				.status("Windy");
//		
//		HourlyWeather hourlyWeather2 = new HourlyWeather()
//				.location(location).hourOfDay(9)
//				.temperature(20)
//				.precipitation(20)
//				.status("Windy");
//		hourlyWeathers.add(hourlyWeather2);
//		hourlyWeathers.add(hourlyWeather);
//		
//		Location updatedLocation = locationRepository.save(location);
//		assertThat(updatedLocation.getHourlyweathers()).isNotEmpty();
//
//	}
//	
//	@Test
//	public void testFindByCountryCodeAndCityName() {
//		String countryCode = "IN";
//		String cityName ="Mumbai";
//		
//		Location locationInDB = locationRepository.findByCountryCodeAndCityName(countryCode, cityName);
//		assertThat(locationInDB).isNotNull();
//	}
	
//	@Test
//	public void testAddDailyWeather() {
//		Location location = locationRepository.findById("NYC_USA").get();
//		List<DailyWeather> dailyWeathers = location.getDailyweathers();
//		
//		
//		DailyWeather forecast1 = new DailyWeather();
//		forecast1.location(location)
//		.dayOfMonth(16)
//		.month(7)
//		.minTemp(25)
//		.maxTemp(35)
//		.status("Sunny");
//		
//		DailyWeather forecast2 = new DailyWeather();
//		forecast2.location(location)
//		.dayOfMonth(17)
//		.month(7)
//		.minTemp(28)
//		.maxTemp(38)
//		.status("Sunny");
//		
//		dailyWeathers.add(forecast1);
//		dailyWeathers.add(forecast2);
//		
//		Location updatedLocation = locationRepository.save(location);
//		List<DailyWeather> updatedDailyWeathers = updatedLocation.getDailyweathers();
//		assertThat(updatedDailyWeathers).isNotEmpty();
//		assertThat(updatedDailyWeathers.size()).isEqualTo(2);
//	}
	
	@Test
	public void testFindByPage() {
		int pageSize = 2;
		int pageNumber = 1;
		
		Sort sort = Sort.by("cityName").ascending();
		
		Pageable pageable = PageRequest.of(pageNumber -1,2,sort);
		Page<Location> pages = locationRepository.findByPageAndUnTrashed(pageable);
		List<Location> listLocation = pages.getContent();
		assertThat(listLocation.size()).isEqualTo(pageSize);
		assertThat(listLocation.size()).isNotEqualTo(0);
		pages.forEach(System.out::println);
		listLocation.forEach((item) -> System.out.println(item));
	}
}
