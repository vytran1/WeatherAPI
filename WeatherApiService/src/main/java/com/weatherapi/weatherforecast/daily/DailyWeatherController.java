package com.weatherapi.weatherforecast.daily;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.weatherapi.weatherforecast.CommonUtility;
import com.weatherapi.weatherforecast.GeoLocationException;
import com.weatherapi.weatherforecast.GeolocationService;
import com.weatherapi.weatherforecast.common.DailyWeather;
import com.weatherapi.weatherforecast.common.Location;
import com.weatherapi.weatherforecast.full.FullWeatherController;
import com.weatherapi.weatherforecast.hourly.HourlyWeatherController;
import com.weatherapi.weatherforecast.realtime.RealtimeWeatherController;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/v1/daily")
public class DailyWeatherController {
   
	
	@Autowired
	private DailyWeatherService dailyWeatherService;
	
	@Autowired
	private GeolocationService geolocationService;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@GetMapping
	public ResponseEntity<?> getByIPAddress(HttpServletRequest request){
		String ipAddress = CommonUtility.getIpAddress(request);
		try {
			Location location = geolocationService.getLocation(ipAddress);
			List<DailyWeather> listDailyWeather = dailyWeatherService.listByLocationCode(location);
			if(listDailyWeather.isEmpty()) {
				return ResponseEntity.noContent().build();
			}else {
				DailyWeatherListDTO listDailyWeatherDTOs = convertListEntitiesToListDTO(listDailyWeather);
				listDailyWeatherDTOs.setLocation(location.toString());
				return ResponseEntity.ok(addLinksByIP(listDailyWeatherDTOs));
			}
			
		} catch (GeoLocationException e) {
			// TODO Auto-generated catch block
			return ResponseEntity.badRequest().build();
		}
		
	} 
	
	@GetMapping("/{code}")
	public ResponseEntity<?> getByLocationCode(@PathVariable("code") String code){
		List<DailyWeather> listDailyWeathers = dailyWeatherService.listByLocationCode(code);
		if(listDailyWeathers.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		
		DailyWeatherListDTO dailyWeatherListDTO = convertListEntitiesToListDTO(listDailyWeathers);
		dailyWeatherListDTO.setLocation(code);
		
		EntityModel<DailyWeatherListDTO> linksByLocationCode = addLinksByLocationCode(dailyWeatherListDTO, code);
		
		
		return ResponseEntity.ok().cacheControl(CacheControl.maxAge(6,TimeUnit.HOURS).cachePublic())
				.body(linksByLocationCode);
		
	}
	
	
	@PutMapping("/{code}")
	public ResponseEntity<?> updateDailyWeather(@PathVariable("code") String code,@RequestBody List<DailyWeatherDTO> dailyWeathersDTOInRequest ){
		if(dailyWeathersDTOInRequest.isEmpty()) {
			return ResponseEntity.badRequest().build();
		}
		
		dailyWeathersDTOInRequest.forEach((item) -> System.out.println(item));
		
		//Convert List DTO in request to list entity
		List<DailyWeather> dailyWeathersInRequest = convertListDTOToEntity(dailyWeathersDTOInRequest);
		
		//Update List Entity To Database
		List<DailyWeather> updatedDailyWeathers = dailyWeatherService.updateDailyWeather(code, dailyWeathersInRequest);
		
		updatedDailyWeathers.forEach((item) -> System.out.println(item));
		
		//Convert UpdatedList to ListDTO for Response Result
		DailyWeatherListDTO dailyWeatherListDTO = convertListEntitiesToListDTO(updatedDailyWeathers);
		
		
		return ResponseEntity.ok(addLinksByLocationCode(dailyWeatherListDTO, code));
		
		
	}
	
	
	public DailyWeatherDTO convertEntityToDTO(DailyWeather dailyWeather) {
		DailyWeatherDTO dailyWeatherDTO = modelMapper.map(dailyWeather,DailyWeatherDTO.class);
		return dailyWeatherDTO;
	}
	
	public DailyWeather convertDTOToEntity(DailyWeatherDTO dailyWeatherDTO) {
		DailyWeather dailyWeather = modelMapper.map(dailyWeatherDTO,DailyWeather.class);
		return dailyWeather;
	}
	
	public List<DailyWeather> convertListDTOToEntity(List<DailyWeatherDTO> dailyWeatherDTOInRequest){
		List<DailyWeather> listDailyWeathers = new ArrayList<>();
		for(DailyWeatherDTO item : dailyWeatherDTOInRequest) {
			DailyWeather dailyWeather = convertDTOToEntity(item);
			listDailyWeathers.add(dailyWeather);
		}
		return listDailyWeathers;
	}
	
	public DailyWeatherListDTO convertListEntitiesToListDTO(List<DailyWeather> dailyWeathers) {
		Location location = dailyWeathers.get(0).getId().getLocation();
		DailyWeatherListDTO listDailyWeatherDTO = new DailyWeatherListDTO();
		listDailyWeatherDTO.setLocation(location.toString());
		for(DailyWeather dailyWeather : dailyWeathers) {
			DailyWeatherDTO dailyWeatherDTO = convertEntityToDTO(dailyWeather);
			listDailyWeatherDTO.addDailyWeatherDTO(dailyWeatherDTO);
		}
		return listDailyWeatherDTO;
	}
	
	private EntityModel<DailyWeatherListDTO> addLinksByIP(DailyWeatherListDTO dto) throws GeoLocationException {
		EntityModel<DailyWeatherListDTO> entityModel = EntityModel.of(dto);
		entityModel.add(
				linkTo(
						methodOn(DailyWeatherController.class).getByIPAddress(null))
				                 .withSelfRel());
		entityModel.add(
				linkTo(
						methodOn(RealtimeWeatherController.class).getRealtimeWeatherByIP(null))
				                 .withRel("realtime_forecast"));
		entityModel.add(
				linkTo(
						methodOn(HourlyWeatherController.class).listHourlyForecastByIPAddress(null))
				                 .withRel("daily_forecast"));
		entityModel.add(
				linkTo(
						methodOn(FullWeatherController.class).getFullWeatherByIPAddress(null))
				                 .withRel("full_forecast"));
		return entityModel;
	}
	
	private EntityModel<DailyWeatherListDTO> addLinksByLocationCode(DailyWeatherListDTO dto,String code){
		EntityModel<DailyWeatherListDTO> entityModel = EntityModel.of(dto);
		entityModel.add(
				linkTo(
						methodOn(DailyWeatherController.class).getByLocationCode(code)).withSelfRel());
				               
		entityModel.add(
				linkTo(
						methodOn(RealtimeWeatherController.class).getRealtimeWeatherByLocationCode(code))
				                 .withRel("realtime_forecast"));
		entityModel.add(
				linkTo(
						methodOn(HourlyWeatherController.class).listHourlyForecastByLocationCode(code,null))
				                 .withRel("daily_forecast"));
		entityModel.add(
				linkTo(
						methodOn(FullWeatherController.class).getFullWeatherByLocationCode(code))
				                 .withRel("full_forecast"));
		return entityModel;
	}
}
