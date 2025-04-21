package com.weatherapi.weatherforecast.hourly;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.weatherapi.weatherforecast.common.HourlyWeather;
import com.weatherapi.weatherforecast.common.HourlyWeatherId;
import com.weatherapi.weatherforecast.common.Location;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class HourlyWeatherRepositoryTest {
    
	
	@Autowired
	private HourlyWeatherRepository hourlyWeatherRepository;
	
	
	@Test
	public void testAdd() {
		String locationCode = "DELHI_IN";
		int hoursOfday = 12;
		Location location = new Location();
		location.code(locationCode);
		
		
		HourlyWeather hourlyWeather = new HourlyWeather()
				.id(location,hoursOfday)
				.temperature(12)
				.precipitation(30)
				.status("Windy");
		
		HourlyWeather updatedForecast = hourlyWeatherRepository.save(hourlyWeather);
		assertThat(updatedForecast.getId().getLocation().getCode()).isEqualTo(locationCode);
	}
	
	@Test
	public void testDelete() {
		String locationCode = "DELHI_IN";
		int hoursOfday = 12;
		Location location = new Location();
		location.code(locationCode);
		HourlyWeatherId hourlyWeatherId = new HourlyWeatherId(hoursOfday,location);
		
		hourlyWeatherRepository.deleteById(hourlyWeatherId);
		
		Optional<HourlyWeather> hourOptional = hourlyWeatherRepository.findById(hourlyWeatherId);
		assertThat(hourOptional).isNotPresent();
	}
	
	@Test
	public void testFindByLocationCode() {
		String locationCode = "MBMH_IN";
		Integer currentHour = 10;
		
		List<HourlyWeather> listResult = hourlyWeatherRepository.findByLocationCode(locationCode,currentHour);
		assertThat(listResult.size()).isEqualTo(0);
//		for(HourlyWeather item : listResult) {
//			System.out.println("Temperature: " + item.getTemperature());
//		}
	}
}
