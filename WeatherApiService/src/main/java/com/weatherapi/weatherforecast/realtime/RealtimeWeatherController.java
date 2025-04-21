package com.weatherapi.weatherforecast.realtime;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.weatherapi.weatherforecast.CommonUtility;
import com.weatherapi.weatherforecast.GeoLocationException;
import com.weatherapi.weatherforecast.GeolocationService;
import com.weatherapi.weatherforecast.common.Location;
import com.weatherapi.weatherforecast.common.RealtimeWeather;
import com.weatherapi.weatherforecast.daily.DailyWeatherController;
import com.weatherapi.weatherforecast.full.FullWeatherController;
import com.weatherapi.weatherforecast.hourly.HourlyWeatherController;
import com.weatherapi.weatherforecast.location.LocationNotFoundException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/v1/realtime")
public class RealtimeWeatherController {
   
	@Autowired
	private GeolocationService geolocationService;
	
	@Autowired
	private RealtimeWeatherService realtimeWeatherService;
	
	
	@Autowired
	private ModelMapper modelMapper;
	
    private static final Logger logger = LoggerFactory.getLogger(CommonUtility.class);

	
	@GetMapping()
	public ResponseEntity<?> getRealtimeWeatherByIP(HttpServletRequest request){
		String ipAddress = CommonUtility.getIpAddress(request);
		
		try {
			Location locationFromIP = geolocationService.getLocation(ipAddress);
			RealtimeWeather realtimeWeather = realtimeWeatherService.getByCountryCodeAndCityName(locationFromIP);
			RealtimeWeatherDTO  resultDTO = modelMapper.map(realtimeWeather,RealtimeWeatherDTO.class);
			//String expectedLocation = locationFromIP.getCityName() + ", " + locationFromIP.getRegionName() + ", " + locationFromIP.getCountryName();
			//resultDTO.setLocation(expectedLocation);
			return ResponseEntity.ok(addLinkByIP(resultDTO));
		} catch (GeoLocationException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(),e);
			return ResponseEntity.badRequest().build();
		} catch (LocationNotFoundException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(),e);
			return ResponseEntity.notFound().build();
		}
	}
	
	
	@GetMapping("/{code}")
	public ResponseEntity<?> getRealtimeWeatherByLocationCode(@PathVariable("code") String locationCode){
		try {
			RealtimeWeather resultInDB = realtimeWeatherService.getByLocationCode(locationCode);
			
			var lastModifiedTime = resultInDB.getLastUpdated().toInstant();
			
			HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
			
			Optional<String> ifModifiedSince = Optional.ofNullable(request.getHeader("If-Modified-Since"));
			
			if(ifModifiedSince.isPresent()) {
				Instant ifModifiedSinceTime = Instant.from(
						DateTimeFormatter.RFC_1123_DATE_TIME.withZone(ZoneOffset.UTC).parse(ifModifiedSince.get()));
				
				if(!lastModifiedTime.isAfter(ifModifiedSinceTime)) {
					return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
				}
				
			}
			
			RealtimeWeatherDTO realtimeWeatherDTO = modelMapper.map(resultInDB,RealtimeWeatherDTO.class);
			
			RealtimeWeatherDTO linkByLocationCode = addLinkByLocationCode(realtimeWeatherDTO, locationCode);
			
			
			
			return ResponseEntity.ok()
					.cacheControl(CacheControl.maxAge(30,TimeUnit.MINUTES))
					.lastModified(lastModifiedTime)
					.body(linkByLocationCode);
		} catch (LocationNotFoundException e) {
			// TODO Auto-generated catch block
			return ResponseEntity.notFound().build();
		}
	}
	
	
	
	@PutMapping("/{code}")
	public ResponseEntity<?> updateRealtimeWeather(@PathVariable("code") String code,@RequestBody @Valid RealtimeWeatherDTO realtimeWeatherDTO){
		try {
			RealtimeWeather realtimeWeather = converDTOToEntity(realtimeWeatherDTO);
			realtimeWeather.setLocationCode(code);
			
			
			RealtimeWeather updatedRealtimeWeather = realtimeWeatherService.update(code, realtimeWeather);
			
			
			
			RealtimeWeatherDTO resultDTO = convertToDTO(updatedRealtimeWeather);
			RealtimeWeatherDTO linkByLocationCode = addLinkByLocationCode(resultDTO, code);
			
			
			return ResponseEntity.ok(linkByLocationCode);
					
		} catch (LocationNotFoundException e) {
			// TODO Auto-generated catch block
			return ResponseEntity.notFound().build();
		}
	}
	
	private RealtimeWeatherDTO convertToDTO(RealtimeWeather realtimeWeather) {
		return modelMapper.map(realtimeWeather,RealtimeWeatherDTO.class);
	}
	
	private RealtimeWeather converDTOToEntity(RealtimeWeatherDTO realtimeWeatherDTO) {
		RealtimeWeather realtimeWeather = modelMapper.map(realtimeWeatherDTO,RealtimeWeather.class);
		return realtimeWeather;
	}
	
	private RealtimeWeatherDTO addLinkByIP(RealtimeWeatherDTO dto) throws GeoLocationException {
		dto.add(
				linkTo(
						methodOn(RealtimeWeatherController.class).getRealtimeWeatherByIP(null))
				                 .withSelfRel());
		dto.add(
				linkTo(
						methodOn(HourlyWeatherController.class).listHourlyForecastByIPAddress(null))
				                 .withRel("hourly_forecast"));
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
	
	private RealtimeWeatherDTO addLinkByLocationCode(RealtimeWeatherDTO dto,String locationCode) {
		dto.add(
				linkTo(
						methodOn(RealtimeWeatherController.class).getRealtimeWeatherByLocationCode(locationCode))
				                 .withSelfRel());
		dto.add(
				linkTo(
						methodOn(HourlyWeatherController.class).listHourlyForecastByLocationCode(locationCode,null))
				                 .withRel("hourly_forecast"));
		dto.add(
				linkTo(
						methodOn(DailyWeatherController.class).getByLocationCode(locationCode))
				                 .withRel("daily_forecast"));
		dto.add(
				linkTo(
						methodOn(FullWeatherController.class).getFullWeatherByLocationCode(locationCode))
				                 .withRel("full_forecast"));
		return dto;
	}
}
