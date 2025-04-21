package com.weatherapi.weatherforecast.full;

import java.util.concurrent.TimeUnit;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

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

import com.weatherapi.weatherforecast.BadRequestException;
import com.weatherapi.weatherforecast.CommonUtility;
import com.weatherapi.weatherforecast.GeoLocationException;
import com.weatherapi.weatherforecast.GeolocationService;
import com.weatherapi.weatherforecast.common.Location;
import com.weatherapi.weatherforecast.daily.DailyWeatherDTO;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/v1/full")
public class FullWeatherController {
    
	
	@Autowired
	private GeolocationService geolocationService;
	
	@Autowired
	private FullWeatherService fullWeatherService;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private FullWeatherModelAssembler fullWeatherModelAssembler;
	
	@GetMapping
	public ResponseEntity<?> getFullWeatherByIPAddress(HttpServletRequest request) throws GeoLocationException{
		String IPAddress = CommonUtility.getIpAddress(request);
		Location location = geolocationService.getLocation(IPAddress);
		
		
		Location locationInDB  = fullWeatherService.getByLocation(location);
		
		FullWeatherDTO result = convertEntityToDTO(locationInDB);
		return ResponseEntity.ok(fullWeatherModelAssembler.toModel(result));
	}
	
	
	@GetMapping("/{code}")
	public ResponseEntity<?> getFullWeatherByLocationCode(@PathVariable("code") String code ){
		
		Location locationInDB =  fullWeatherService.get(code);
		
		FullWeatherDTO result = convertEntityToDTO(locationInDB);
		
		
		EntityModel<FullWeatherDTO> linksByLocation = addLinksByLocation(result, code);
		
		
		return ResponseEntity.ok()
				.cacheControl(CacheControl.maxAge(30,TimeUnit.MINUTES).cachePublic())
				.body(linksByLocation);
	}
	
	@PutMapping("/{code}")
	public ResponseEntity<?> updateFullWeatherByLocationCode(@PathVariable("code") String code, 
			                 @RequestBody FullWeatherDTO dto) throws BadRequestException{
		if(dto.getDailyweathers().isEmpty()) {
			throw new BadRequestException("List Daily Weather Forecast can not be null");
		}
		
		if(dto.getHourlyweathers().isEmpty()) {
			throw new BadRequestException("List Hourly Weather Forecast can not be null");
		}
		
		Location locationInRequest = convertDTOToEntity(dto);
		
		
		Location updatedLocation = fullWeatherService.updateFullWeatherData(code, locationInRequest);
		
		FullWeatherDTO resultDTO = convertEntityToDTO(updatedLocation);
		
		
		return ResponseEntity.ok(resultDTO);
	}
	
	
	public FullWeatherDTO convertEntityToDTO(Location location) {
		//location.getRealtimeWeather().setLocation(null);
		FullWeatherDTO dto = modelMapper.map(location,FullWeatherDTO.class); 
		dto.getRealtimeWeather().setLocation(null);
		return dto ;
	}
	
	public Location convertDTOToEntity(FullWeatherDTO dto) {
		return modelMapper.map(dto,Location.class);
	}
	
	private EntityModel<FullWeatherDTO> addLinksByLocation(FullWeatherDTO dto, String locationCode) {
		return EntityModel.of(dto)
				.add(linkTo(
						methodOn(FullWeatherController.class).getFullWeatherByLocationCode(locationCode))
							.withSelfRel());		
	}
}
