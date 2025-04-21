package com.weatherapi.weatherforecast.base;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.weatherapi.weatherforecast.GeoLocationException;
import com.weatherapi.weatherforecast.daily.DailyWeatherController;
import com.weatherapi.weatherforecast.full.FullWeatherController;
import com.weatherapi.weatherforecast.hourly.HourlyWeatherController;
import com.weatherapi.weatherforecast.location.LocationApiController;
import com.weatherapi.weatherforecast.realtime.RealtimeWeatherController;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class MainController {
    
	
	@GetMapping()
	public ResponseEntity<RootEntity> handleBaseURL(HttpServletRequest request) throws GeoLocationException{
		return ResponseEntity.ok(createRootEntity(request));
	}
	
	
	private RootEntity createRootEntity(HttpServletRequest request) throws GeoLocationException {
		RootEntity rootEntity = new RootEntity();
		
		String locationURL = linkTo(methodOn(LocationApiController.class).listLocations()).toString();
		String locationByCodeURL = linkTo(methodOn(LocationApiController.class).getLocation("code")).toString();
		String realtimeWeatherByIPURL = linkTo(methodOn(RealtimeWeatherController.class).getRealtimeWeatherByIP(request)).toString();
		String realtimeWeatherByCodeURL = linkTo(methodOn(RealtimeWeatherController.class).getRealtimeWeatherByLocationCode("{code}")).toString();
		String hourlyForecastByIPURL = linkTo(methodOn(HourlyWeatherController.class).listHourlyForecastByIPAddress(request)).toString();
		String hourlyForecastByCodeURL = linkTo(methodOn(HourlyWeatherController.class).listHourlyForecastByLocationCode("{code}", request)).toString();
		String dailyForecastByIpURl = linkTo(methodOn(DailyWeatherController.class).getByIPAddress(request)).toString();
		String dailyForecastByCodeURL = linkTo(methodOn(DailyWeatherController.class).getByLocationCode("{code}")).toString();
		String fullWeatherByIPURL = linkTo(methodOn(FullWeatherController.class).getFullWeatherByIPAddress(request)).toString();
		String fullWeatherByCodeURL = linkTo(methodOn(FullWeatherController.class).getFullWeatherByLocationCode("{code}")).toString();
		
		rootEntity.setLocationByCodeURL(locationByCodeURL);
		rootEntity.setLocationURL(locationURL);
		
		rootEntity.setRealtimeWeatherByIpURL(realtimeWeatherByIPURL);
		rootEntity.setRealtimeWeatherByCodeUrl(realtimeWeatherByCodeURL);
		
		rootEntity.setHourlyForecastByIpUrl(hourlyForecastByIPURL);
		rootEntity.setHourlyForecastByCodeUrl(hourlyForecastByCodeURL);
  
		rootEntity.setDailyForecastByIpUrl(dailyForecastByIpURl);
		rootEntity.setDailyForecastByCodeUrl(dailyForecastByCodeURL);
		
		rootEntity.setFullWeatherByIpUrl(fullWeatherByIPURL);
		rootEntity.setFullWeatherByCodeUrl(fullWeatherByCodeURL);
		
		
		
		return rootEntity;
	}
}
