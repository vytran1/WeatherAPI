package com.weatherapi.weatherforecast.realtime;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.weatherapi.weatherforecast.common.RealtimeWeather;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class RealtimeWeatherRepositoryTest {
    
	@Autowired
	private RealtimeWeatherRepository realtimeWeatherRepository;
	
	@Test
	public void testUpdateRealtimeWeather() {
		String locationCode = "NYC_USA";
		
		RealtimeWeather realtimeWeather = realtimeWeatherRepository.findById(locationCode).get();
		realtimeWeather.setTemperature(11);
		realtimeWeather.setHumidity(61);
		realtimeWeather.setPrecipitation(71);
		realtimeWeather.setStatus("Sunny");
		realtimeWeather.setWindSpeed(11);
		realtimeWeather.setLastUpdated(new Date());
		RealtimeWeather updatedRealtime =  realtimeWeatherRepository.save(realtimeWeather);
		assertThat(updatedRealtime.getTemperature()).isEqualTo(11);
		
	}
	
	@Test
	public void testFindByCountryCodeAndCityNotFound() {
		String countryCode = "JP";
		String cityName = "Tokyo";
		RealtimeWeather realtimeWeather = realtimeWeatherRepository.findByCountryCodeAndCity(countryCode, cityName);
		assertThat(realtimeWeather).isNull();
	}
	
	@Test
	public void testFindByCountryCodeAndCityFound() {
		String countryCode = "US";
		String cityName = "New York City";
		RealtimeWeather realtimeWeather = realtimeWeatherRepository.findByCountryCodeAndCity(countryCode, cityName);
		assertThat(realtimeWeather).isNotNull();
		System.out.println(realtimeWeather.getTemperature());
	}
	
	@Test
	public void testFindByLocationCodeNotFound() {
		String locationCode = "ABC";
		RealtimeWeather resultInDB = realtimeWeatherRepository.findByLocationCode(locationCode);
		assertThat(resultInDB).isNull();
	}
	
	@Test
	public void testFindByLocationCodeTrashedNotFound() {
		String locationCode = "DELHI_IN";
		RealtimeWeather resultInDB = realtimeWeatherRepository.findByLocationCode(locationCode);
		assertThat(resultInDB).isNull();
	}
	
	@Test
	public void testFindByLocationCodeFound() {
		String locationCode = "NYC_USA";
		RealtimeWeather resultInDB = realtimeWeatherRepository.findByLocationCode(locationCode);
		assertThat(resultInDB).isNotNull();
		System.out.println(resultInDB);
	}
}
