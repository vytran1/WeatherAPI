package com.weatherapi.weatherforecast.daily;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.weatherapi.weatherforecast.common.DailyWeather;
import com.weatherapi.weatherforecast.common.DailyWeatherId;
import com.weatherapi.weatherforecast.common.Location;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class DailyWeatherRepositoryTest {
    
	
	@Autowired
	private DailyWeatherRepository dailyWeatherRepository;
	
	
//	@Test
//	public void testAdd() {
//		String locationCode ="NYC_USA";
//		Location location = new Location();
//		location.setCode(locationCode);
//		
//		DailyWeather forecast1 = new DailyWeather();
//		forecast1.location(location)
//		.dayOfMonth(19)
//		.month(8)
//		.minTemp(27)
//		.maxTemp(39)
//		.precipitation(40)
//		.status("Sunny");
//		
//		DailyWeather saveDailyWeather = dailyWeatherRepository.save(forecast1);
//		assertThat(saveDailyWeather).isNotNull();
//	}
//	
//	@Test
//	public void testDelete() {
//		Location location = new Location();
//		location.setCode("NYC_USA");
//		DailyWeatherId dailyWeatherId = new DailyWeatherId(17,7,location);
//		dailyWeatherRepository.deleteById(dailyWeatherId);
//		
//		Optional<DailyWeather> dailyWeather = dailyWeatherRepository.findById(dailyWeatherId);
//		assertThat(dailyWeather).isNotPresent();
//	}
	
	@Test 
	public void testFindByLocationCode() {
		String locationCode ="NYC_USA";
		List<DailyWeather> listDailyWeather = dailyWeatherRepository.findByLocationCode(locationCode);
		
		assertThat(listDailyWeather.size()).isEqualTo(2);
		listDailyWeather.forEach((item) -> System.out.println(item.getPrecipitation()));
	}
	
}
