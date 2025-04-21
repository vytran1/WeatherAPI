package com.weatherapi.weatherforecast.location;

import java.net.URI;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.PagedModel.PageMetadata;
import org.springframework.hateoas.server.core.Relation;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.weatherapi.weatherforecast.BadRequestException;
import com.weatherapi.weatherforecast.CommonUtility;
import com.weatherapi.weatherforecast.common.Location;
import com.weatherapi.weatherforecast.daily.DailyWeatherController;
import com.weatherapi.weatherforecast.full.FullWeatherController;
import com.weatherapi.weatherforecast.hourly.HourlyWeatherController;
import com.weatherapi.weatherforecast.realtime.RealtimeWeatherController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("/v1/locations")
@Validated
public class LocationApiController {
    private Map<String,String> propertiesMap = Map.of("code","code"
    		                                          ,"city_name","cityName"
    		                                          ,"region_name","regionName"
    		                                          ,"country_code","countryCode"
    		                                          ,"country_name","countryName"
    		                                          ,"enabled","enabled");
	
	@Autowired
	private LocationService localService;
	
	
	@Autowired
	private ModelMapper modelMapper;
	
	@PostMapping()
	public ResponseEntity<?> addLocation(@RequestBody @Valid Location location){
		Location savedLocation = localService.add(location);
		URI uri = URI.create("/v1/locations/" + savedLocation.getCode());
		LocationDTO locationDTO = convertToDTO(savedLocation);
		addLinksToItemWithoutHttpRequest(locationDTO);
		return ResponseEntity.created(uri).body(locationDTO);
	}
	
	@Deprecated
	public ResponseEntity<List<?>> listLocations(){
		List<Location> listLocation = localService.list();
		List<LocationDTO> listLocationDTO = convertToListDTO(listLocation);
		if(listLocation.isEmpty()) {
//			String ipAddres = CommonUtility.getIpAddress(request);
//			System.out.println(ipAddres);
			return ResponseEntity.noContent().build();
		}else {
//			String ipAddres = CommonUtility.getIpAddress(request);
//			System.out.println(ipAddres);
			return new ResponseEntity<List<?>>(listLocationDTO,HttpStatus.OK);
		}
	}
	
	@GetMapping
	public ResponseEntity<?> listLocations(@RequestParam(value = "pageNum", required = false, defaultValue = "1" ) 
	                                                    @Min(value = 1) int pageNum,
			                               @RequestParam(value = "pageSize", required = false, defaultValue = "2") 
	                                                    @Min(value = 2) @Max(value = 10) int pageSize,
			                               @RequestParam(value = "sortField", required = false, defaultValue = "code") String sortOptions,
			                               @RequestParam(value = "enabled", required = false, defaultValue = "") String enabled,
			                               @RequestParam(value = "region_name", required = false, defaultValue = "") String regionName,
			                               @RequestParam(value = "country_code", required = false, defaultValue = "") String countryCode,
			                               HttpServletRequest request
			                               


			                               
			
			) throws BadRequestException{
		
		sortOptions = validateSortOptions(sortOptions);
		
		
		Map<String, Object> filterFields = getMapFilterFields(enabled, regionName, countryCode);
		
		Page<Location> pages = localService.listByPage(pageNum, pageSize,sortOptions, filterFields);
		List<Location> listLocations = pages.getContent();
		if(listLocations.isEmpty()) {
			return ResponseEntity.noContent().build();
		}else {
			
			listLocations.forEach(System.out::println);
			List<LocationDTO> listLocationsDTO = convertToListDTO(listLocations);
			
			CollectionModel<LocationDTO> responseBody = addPageMetadataAndLinksToCollection(listLocationsDTO,pages,sortOptions,enabled,regionName,countryCode,request);
			
			
			
			
			return ResponseEntity.ok().cacheControl(CacheControl.maxAge(7,TimeUnit.DAYS).cachePublic()).body(responseBody);
		}
	}

	private Map<String, Object> getMapFilterFields(String enabled, String regionName, String countryCode) {
		Map<String,Object> filterFields = new HashMap<>();
		
		if(!"".equals(enabled)) {
			filterFields.put("enabled",Boolean.parseBoolean(enabled));
		}
		
		if(!"".equals(regionName)) {
			filterFields.put("regionName",regionName);
		}
		
		if(!"".equals(countryCode)) {
			filterFields.put("countryCode",countryCode);
		}
		return filterFields;
	}

	private String validateSortOptions(String sortOptions) throws BadRequestException {
		String translatedSortOptions = sortOptions;
		String[] listOptions = sortOptions.split(",");
		if(listOptions.length > 1) {
			for(int i=0; i<listOptions.length;i++) {
				String actualSortField = listOptions[i].replace("-","");
				if(!propertiesMap.containsKey(actualSortField)) {
					throw new BadRequestException("Invalid Sort Field: " + actualSortField);
				}
				translatedSortOptions = translatedSortOptions.replace(actualSortField,propertiesMap.get(actualSortField));
				System.out.println("Sorting Field Name In Controller: " + translatedSortOptions);
			}
			
			
		}else {
			String actualSortField = sortOptions.replace("-","");
			if(!propertiesMap.containsKey(actualSortField)) {
				throw new BadRequestException("Invalid Sort Field: " + actualSortField);
			}
			//Could not resolve region_name attribute the reason is that when we sent request from postman, the sortOptions,for example, was region_name. 
			//The problem is that location contains regionName instead of region_name
			translatedSortOptions = translatedSortOptions.replace(actualSortField,propertiesMap.get(actualSortField));
			System.out.println("Sorting Field Name In Controller: " + translatedSortOptions);
		}
		return translatedSortOptions;
	}
	
	private CollectionModel<LocationDTO> addPageMetadataAndLinksToCollection(List<LocationDTO> listLocationDTOs, Page<Location> pageInfo,String sortField,String enabled,String regionName,String countryCode,HttpServletRequest request) throws BadRequestException{
		
		String actualEnabled = "".equals(enabled) ? null : enabled;
		String actualRegionName = "".equals(regionName) ? null : regionName;
		String actualCountryCode = "".equals(countryCode) ? null : countryCode;


		
		//Add links to individuals item
		for(LocationDTO dto : listLocationDTOs) {
			addLinksToItem(request, dto);
		}
		
		//Add page Meta data
		int pageSize = pageInfo.getSize();
		int pageNum = pageInfo.getNumber() + 1;
		int totalPages = pageInfo.getTotalPages();
		long totalElements = pageInfo.getTotalElements();
		
		
		PageMetadata pageMetadata = new PageMetadata(pageSize, pageNum, totalElements);
		
		CollectionModel<LocationDTO> collectionModelLocation = PagedModel.of(listLocationDTOs, pageMetadata);
		
		//Add self links to collectionModelLocation
		collectionModelLocation.add(linkTo(methodOn(LocationApiController.class).listLocations(pageNum, pageSize, sortField,actualEnabled,actualRegionName,actualCountryCode,request)).withSelfRel());
		
		if(pageNum > 1 ) {
			//add first link to collectionModelLocation if it is not first page
			collectionModelLocation.add(linkTo(methodOn(LocationApiController.class).listLocations(1, pageSize, sortField,actualEnabled,actualRegionName,actualCountryCode,request)).withRel(IanaLinkRelations.FIRST));
            //add previous link to collection if it is not first page
			collectionModelLocation.add(linkTo(methodOn(LocationApiController.class).listLocations(pageNum - 1, pageSize, sortField,actualEnabled,actualRegionName,actualCountryCode,request)).withRel(IanaLinkRelations.PREVIOUS));

		}
		
		if(pageNum < totalPages) {
			//add next link to collection if it is not last page
		    collectionModelLocation.add(linkTo(methodOn(LocationApiController.class).listLocations(pageNum + 1, pageSize, sortField,actualEnabled,actualRegionName,actualCountryCode,request)).withRel(IanaLinkRelations.NEXT));
	        //add last link to collection if it is not last page
		    collectionModelLocation.add(linkTo(methodOn(LocationApiController.class).listLocations(totalPages, pageSize, sortField,actualEnabled,actualRegionName,actualCountryCode,request)).withRel(IanaLinkRelations.LAST));
		}
		
		return collectionModelLocation;
		
	}

	private void addLinksToItem(HttpServletRequest request, LocationDTO dto) {
		dto.add(linkTo(methodOn(LocationApiController.class).getLocation(dto.getCode())).withSelfRel());
		dto.add(linkTo(methodOn(RealtimeWeatherController.class).getRealtimeWeatherByLocationCode(dto.getCode())).withRel("realtime"));
		dto.add(linkTo(methodOn(HourlyWeatherController.class).listHourlyForecastByLocationCode(dto.getCode(),request)).withRel("hourly_forecast"));
		dto.add(linkTo(methodOn(DailyWeatherController.class).getByLocationCode(dto.getCode())).withRel("daily_forecast"));
		dto.add(linkTo(methodOn(FullWeatherController.class).getFullWeatherByLocationCode(dto.getCode())).withRel("full_forecast"));
	}
	
	private void addLinksToItemWithoutHttpRequest(LocationDTO dto) {
		dto.add(linkTo(methodOn(LocationApiController.class).getLocation(dto.getCode())).withSelfRel());
		dto.add(linkTo(methodOn(RealtimeWeatherController.class).getRealtimeWeatherByLocationCode(dto.getCode())).withRel("realtime"));
		dto.add(linkTo(methodOn(HourlyWeatherController.class).listHourlyForecastByLocationCode(dto.getCode(),null)).withRel("hourly_forecast"));
		dto.add(linkTo(methodOn(DailyWeatherController.class).getByLocationCode(dto.getCode())).withRel("daily_forecast"));
		dto.add(linkTo(methodOn(FullWeatherController.class).getFullWeatherByLocationCode(dto.getCode())).withRel("full_forecast"));
	}
	
	
	
	
	@GetMapping("/{code}")
	public ResponseEntity<?> getLocation(@PathVariable("code") String code){
		Location location = localService.get(code);
		
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
		
		String ifNonMatch = request.getHeader("If-None-Match");
		
		String eTag = "\"" + Objects.hash(location.getCode(),location.getCountryCode(),location.getCountryName(),location.getCityName(),location.getRegionName()) + "\"";
		
		if(eTag.equals(ifNonMatch)) {
			return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
		}
		
		LocationDTO dtoObject = convertToDTO(location);
		
		addLinksToItemWithoutHttpRequest(dtoObject);
		
		return ResponseEntity.ok()
				.cacheControl(CacheControl.maxAge(7,TimeUnit.DAYS).cachePublic())
				.eTag(eTag)
				.body(dtoObject);
	}
	
	@PutMapping
	public ResponseEntity<?> updateLocation(@RequestBody @Valid Location location){
			Location updatedLocation = localService.update(location);
			LocationDTO locationDTO = convertToDTO(updatedLocation);
			return ResponseEntity.ok(locationDTO);
		
	}
	
	@DeleteMapping("/{code}")
	public ResponseEntity<?> deleteLocation(@PathVariable("code") String code){
			localService.deleteLocationByCode(code);
			return ResponseEntity.noContent().build();
		
	}
	
	private LocationDTO convertToDTO(Location location) {
		return modelMapper.map(location,LocationDTO.class);
	} 
	
	private List<LocationDTO> convertToListDTO(List<Location> list){
		List<LocationDTO> listDtos = new ArrayList<>();
		list.forEach((item) -> {
			LocationDTO locationDTO = convertToDTO(item);
			listDtos.add(locationDTO);
		});
		return listDtos;
	}
}
