package com.weatherapi.weatherforecast;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MatchingStrategy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.weatherapi.weatherforecast.common.DailyWeather;
import com.weatherapi.weatherforecast.common.HourlyWeather;
import com.weatherapi.weatherforecast.common.Location;
import com.weatherapi.weatherforecast.common.RealtimeWeather;
import com.weatherapi.weatherforecast.daily.DailyWeatherDTO;
import com.weatherapi.weatherforecast.full.FullWeatherDTO;
import com.weatherapi.weatherforecast.hourly.HourlyWeatherDTO;
import com.weatherapi.weatherforecast.realtime.RealtimeWeatherDTO;
import com.weatherapi.weatherforecast.security.RsaKeyProperties;

@SpringBootApplication
@EnableConfigurationProperties(RsaKeyProperties.class)
@EnableCaching
public class WeatherApiServiceApplication {
    
	@Bean
	public ModelMapper getModelMapper() {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		
		
		configureMappingForHourlyWeather(modelMapper);
		
		
		configureMappingForDailyWeather(modelMapper);
		
		
		configureMappingForFullWeather(modelMapper);
		
		
		var typemap6 = modelMapper.typeMap(RealtimeWeatherDTO.class,RealtimeWeather.class);
		typemap6.addMappings(m -> m.skip(RealtimeWeather::setLocation));
		return modelMapper;
	}


	private void configureMappingForFullWeather(ModelMapper modelMapper) {
		var typemap5 = modelMapper.typeMap(Location.class,FullWeatherDTO.class);
		typemap5.addMapping(src -> src.toString(),(dest,value) -> dest.setLocation(value != null ? (String) value : ""));
	}


	private void configureMappingForDailyWeather(ModelMapper modelMapper) {
		var typemap3 = modelMapper.typeMap(DailyWeather.class,DailyWeatherDTO.class);
		typemap3.addMapping(src -> src.getId().getDayOfMonth(),(dest,value) -> dest.setDayOfMonth(value != null ? (int) value : 0));
		typemap3.addMapping(src -> src.getId().getMonth(),(dest,value) -> dest.setMonth(value != null ? (int) value : 0));
		var typemap4 = modelMapper.typeMap(DailyWeatherDTO.class,DailyWeather.class);
		typemap4.addMapping(src -> src.getDayOfMonth(),(dest,value) -> dest.getId().setDayOfMonth(value != null ? (int) value : 0));
		typemap4.addMapping(src -> src.getMonth(),(dest,value) -> dest.getId().setMonth(value != null ? (int) value : 0));
	}


	private void configureMappingForHourlyWeather(ModelMapper modelMapper) {
		var typemap1 =  modelMapper.typeMap(HourlyWeather.class,HourlyWeatherDTO.class);
		typemap1.addMapping(src -> src.getId().getHourOfDay(),HourlyWeatherDTO::setHourOfDay);
		var typemap2 =  modelMapper.typeMap(HourlyWeatherDTO.class,HourlyWeather.class);
		typemap2.addMapping(src -> src.getHourOfDay(),(dest,value) -> dest.getId().setHourOfDay(value != null ? (int) value : 0));
	}
	
	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
		return objectMapper;
	}
	
	public static void main(String[] args) {
		
		SpringApplication.run(WeatherApiServiceApplication.class, args);
	}
}
