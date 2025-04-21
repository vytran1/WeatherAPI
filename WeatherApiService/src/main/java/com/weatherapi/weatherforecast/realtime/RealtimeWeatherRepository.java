package com.weatherapi.weatherforecast.realtime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.weatherapi.weatherforecast.common.RealtimeWeather;

public interface RealtimeWeatherRepository extends JpaRepository<RealtimeWeather,String> {
   
	@Query("SELECT r FROM RealtimeWeather r WHERE r.location.countryCode = ?1 AND r.location.cityName = ?2")
	public RealtimeWeather findByCountryCodeAndCity(String countryCode,String city);
	
	
	@Query("SELECT r FROM RealtimeWeather r WHERE r.id = ?1 AND r.location.trashed = false")
	public RealtimeWeather findByLocationCode(String locationCode);
}
