package com.weatherapi.weatherforecast.location;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.weatherapi.weatherforecast.common.Location;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class LocationService extends AbstractLocationService  {
   
	
	
	
	
	
	
	
	public LocationService(LocationRepository locationRepository) {
		super();
		this.locationRepository = locationRepository;
	}
	
	@CachePut(cacheNames = "locationCacheByCode",key = "#location.code")
	@CacheEvict(cacheNames = "locationCacheByPagination", allEntries = true)
	public Location add(Location location) {
		return locationRepository.save(location);
	}
	
	@Deprecated
	public Page<Location> listByPage(int pageNum,int pageSize,String sortField){
		Sort sort = Sort.by(sortField).ascending();
		Pageable pageable = PageRequest.of(pageNum - 1, pageSize,sort);
		return locationRepository.findByPageAndUnTrashed(pageable);
	}
	
	@Cacheable("locationCacheByPagination")
	public Page<Location> listByPage(int pageNum,int pageSize,String sortOptions, Map<String,Object> filterFields){
		System.out.println("Sort Field Name When We enter service: " + sortOptions);
		String[] listSortOptions = sortOptions.split(",");
		Sort sort = null;
		for(String option : listSortOptions) {
			System.out.println("Option Sort" + option);
		}
		
		if(listSortOptions.length > 1) { // sort by multiple fields
		   String firstSortField = listSortOptions[0];
		   String actualFieldName = firstSortField.replace("-","");
		   sort = firstSortField.startsWith("-") ? Sort.by(actualFieldName).descending() : Sort.by(actualFieldName).ascending();
		   
		   for(int i = 1; i<listSortOptions.length;i++) {
			   String nextSortField = listSortOptions[i];
			   String nextActualFieldName = nextSortField.replace("-","");
			   sort = sort.and(nextSortField.startsWith("-") ? Sort.by(nextActualFieldName).descending() : Sort.by(nextActualFieldName).ascending());
		   }
		   
			
		}else { //sort by one field
			String actualFieldName = sortOptions.replace("-","");
			System.out.println("Sort Field Name After Replace - character: " + sortOptions);
			sort = sortOptions.startsWith("-") ? Sort.by(actualFieldName).descending() : Sort.by(actualFieldName).ascending();
		}
		
		
		Pageable pageable = PageRequest.of(pageNum - 1, pageSize,sort);
		return locationRepository.listWithFilter(pageable, filterFields);
	}
	
	@Deprecated
	public List<Location> list(){
		return locationRepository.findUntrashed();
	}
	
//	public Location get(String code){
//		Location location = locationRepository.findByCode(code);
//		if(location == null) {
//			throw new LocationNotFoundException("Not exist location with given location code");
//		}
//		return location;
//	}
	
	
	@CachePut(cacheNames = "locationCacheByCode",key = "#location.code")
	@CacheEvict(cacheNames = "locationCacheByPagination", allEntries = true)
	public Location update(Location location) throws LocationNotFoundException {
		String code = location.getCode();
		Location locationInDB = locationRepository.findByCode(code);
		
		if(locationInDB == null) {
			throw new LocationNotFoundException("Not Exist Location with code: " + code);
		}
		
		locationInDB.copyFieldsFromLocation(location);
		
		return locationRepository.save(locationInDB);
	}
	
	
	@CacheEvict(cacheNames = {"locationCacheByPagination","locationCacheByCode"}, allEntries = true)
	public void deleteLocationByCode(String code) throws LocationNotFoundException {
		Location location = locationRepository.findByCode(code);
		if(location == null) {
			throw new LocationNotFoundException("Not exist location with code " + code);
		}
		locationRepository.trashByCode(code);
	}
}
