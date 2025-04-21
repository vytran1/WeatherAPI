package com.weatherapi.weatherforecast.hourly;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.weatherapi.weatherforecast.common.HourlyWeather;
import com.weatherapi.weatherforecast.common.HourlyWeatherId;

public interface HourlyWeatherRepository extends CrudRepository<HourlyWeather,HourlyWeatherId> {
    
	
	@Query("""
			SELECT h From HourlyWeather h WHERE 
			h.id.location.code = ?1 AND 
			h.id.hourOfDay > ?2 AND 
			h.id.location.trashed = false
			""")
	public List<HourlyWeather> findByLocationCode(String code,int currentHour);
	
	
	
}
