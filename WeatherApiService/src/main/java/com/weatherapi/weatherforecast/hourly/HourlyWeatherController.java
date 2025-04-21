package com.weatherapi.weatherforecast.hourly;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.weatherapi.weatherforecast.BadRequestException;
import com.weatherapi.weatherforecast.CommonUtility;
import com.weatherapi.weatherforecast.GeoLocationException;
import com.weatherapi.weatherforecast.GeolocationService;
import com.weatherapi.weatherforecast.common.HourlyWeather;
import com.weatherapi.weatherforecast.common.Location;
import com.weatherapi.weatherforecast.daily.DailyWeatherController;
import com.weatherapi.weatherforecast.full.FullWeatherController;
import com.weatherapi.weatherforecast.location.LocationNotFoundException;
import com.weatherapi.weatherforecast.realtime.RealtimeWeatherController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
@RestController
@RequestMapping("/v1/hourly")
@Validated
public class HourlyWeatherController {
   
	
	@Autowired
	private HourlyWeatherService hourlyWeatherService;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private GeolocationService geolocationService;
	
	
	
	@GetMapping()
	public ResponseEntity<?> listHourlyForecastByIPAddress(HttpServletRequest request){
		String IPAddress = CommonUtility.getIpAddress(request);
		
		try {
			int currentHour = Integer.parseInt(request.getHeader("X-Current-Hour"));
			
			Location location = geolocationService.getLocation(IPAddress);
			
			List<HourlyWeather> listResult = hourlyWeatherService.getByLocatio(location,currentHour);
			
			if(listResult.isEmpty()) {
				return ResponseEntity.noContent().build();
			}else {
				HourlyWeatherListDTO hourlyWeatherListDTO = convertToDTO(listResult);
				return ResponseEntity.ok(addLinksByIP(hourlyWeatherListDTO));
			}
		}catch(NumberFormatException  |GeoLocationException e){
			
			return ResponseEntity.badRequest().build();
		}catch(LocationNotFoundException e) {
			
			return ResponseEntity.notFound().build();
		}
	}
	
	
	@GetMapping("/{code}")
	public ResponseEntity<?> listHourlyForecastByLocationCode(@PathVariable("code") String code,HttpServletRequest request){
		try {
			int currentHour = Integer.parseInt(request.getHeader("X-Current-Hour"));
			List<HourlyWeather> listResultFromDB = hourlyWeatherService.getByLocationCode(code,currentHour);
			if(listResultFromDB.isEmpty()) {
				return ResponseEntity.noContent().build();
			}else {
				HourlyWeatherListDTO hourlyWeatherListDTO = convertToDTO(listResultFromDB);
				
				HourlyWeatherListDTO linksByLocationCode = addLinksByLocationCode(hourlyWeatherListDTO, code);
				
				return ResponseEntity.ok().cacheControl(CacheControl.maxAge(60,TimeUnit.MINUTES).cachePublic())
						.body(linksByLocationCode);
			}
		}catch(NumberFormatException e) {
			return ResponseEntity.badRequest().build();
		}catch(LocationNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}
	
	@PutMapping("/{code}")
    public ResponseEntity<?> updateHourlyForecastByLocationCode(@PathVariable("code") String code,@RequestBody @Valid List<HourlyWeatherDTO> listDTO) throws BadRequestException, org.apache.coyote.BadRequestException{
		if(listDTO.isEmpty()) {
			throw new BadRequestException("Hourly Weather Data can not be empty");
		}
		
		listDTO.forEach((item) -> {
			System.out.println(item.getHourOfDay());
		});
		
		
		List<HourlyWeather> listHourlyWeather = convertDTOtoEntitySource(listDTO);
		
		
		listHourlyWeather.forEach((item) -> {
			System.out.println(item.toString());
		});
		
		
		
		try {
			List<HourlyWeather> listResult = hourlyWeatherService.updateHourlyWeather(code, listHourlyWeather);
			return ResponseEntity.ok(addLinksByLocationCode(convertToDTO(listResult),code));
		}catch (LocationNotFoundException e) {
			// TODO Auto-generated catch block
			return ResponseEntity.notFound().build();
		}
		
		
	}	
	
	private HourlyWeatherListDTO convertToDTO(List<HourlyWeather> hourlyWeatherList) {
		Location location = hourlyWeatherList.get(0).getId().getLocation();
		HourlyWeatherListDTO hourlyWeatherListDto = new HourlyWeatherListDTO();
		hourlyWeatherListDto.setLocation(location.toString());
		hourlyWeatherList.forEach((item) -> {
			HourlyWeatherDTO dto = modelMapper.map(item,HourlyWeatherDTO.class);
			hourlyWeatherListDto.addHourlyWeatherDTO(dto);
		});
		return hourlyWeatherListDto;
	}
	
	
	private List<HourlyWeather> convertDTOtoEntitySource(List<HourlyWeatherDTO> hourlyWeatherDTOs){
		List<HourlyWeather> listHourlyWeathers = new ArrayList<>();
		hourlyWeatherDTOs.forEach((item) -> {
			HourlyWeather hourlyWeather = modelMapper.map(item,HourlyWeather.class);
			listHourlyWeathers.add(hourlyWeather);
		});
		return listHourlyWeathers;
	}
	
	private HourlyWeatherListDTO addLinksByIP(HourlyWeatherListDTO dto) throws GeoLocationException {
		dto.add(
				linkTo(
						methodOn(HourlyWeatherController.class).listHourlyForecastByIPAddress(null))
				                 .withSelfRel());
		dto.add(
				linkTo(
						methodOn(RealtimeWeatherController.class).getRealtimeWeatherByIP(null))
				                 .withRel("realtime_forecast"));
		dto.add(
				linkTo(
						methodOn(DailyWeatherController.class).getByIPAddress(null))
				                 .withRel("daily_forecast"));
		dto.add(
				linkTo(
						methodOn(FullWeatherController.class).getFullWeatherByIPAddress(null))
				                 .withRel("full_forecast"));
		return dto;
	}
	
	private HourlyWeatherListDTO addLinksByLocationCode(HourlyWeatherListDTO dto,String code) {
		dto.add(
				linkTo(
						methodOn(HourlyWeatherController.class).listHourlyForecastByLocationCode(code, null)).withSelfRel());
				               
		dto.add(
				linkTo(
						methodOn(RealtimeWeatherController.class).getRealtimeWeatherByLocationCode(code))
				                 .withRel("realtime_forecast"));
		dto.add(
				linkTo(
						methodOn(DailyWeatherController.class).getByLocationCode(code))
				                 .withRel("daily_forecast"));
		dto.add(
				linkTo(
						methodOn(FullWeatherController.class).getFullWeatherByLocationCode(code))
				                 .withRel("full_forecast"));
		return dto;
	}
}
