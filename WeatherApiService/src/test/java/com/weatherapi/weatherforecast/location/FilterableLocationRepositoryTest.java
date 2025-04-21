package com.weatherapi.weatherforecast.location;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;

import com.weatherapi.weatherforecast.common.Location;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class FilterableLocationRepositoryTest {
    
	@Autowired
	private LocationRepository locationRepository;
	
	@Test
	public void testListWithDefault() {
		int pageSize = 2;
		int pageNum = 0;
		String sortField = "code";
		String regionName = "Maharashtra"; 
		
		
		Sort sort = Sort.by(sortField).ascending();
		Pageable pageable = PageRequest.of(pageNum, pageSize, sort);
		
		Map<String,Object> filterFields = new HashMap<>();
		filterFields.put("regionName",regionName);
		
		
		Page<Location> pages = locationRepository.listWithFilter(pageable,filterFields);
		List<Location> listLocations = pages.getContent();
		
		listLocations.forEach(System.out::println);
		
		assertThat(listLocations.size()).isEqualTo(pages.getSize());
		assertThat(listLocations).isSortedAccordingTo(new Comparator<Location>() {

			@Override
			public int compare(Location o1, Location o2) {
				// TODO Auto-generated method stub
				return o1.getCode().compareTo(o2.getCode());
			}
		});
	}
	
	@Test
	public void testListWithNoFilterSortedByCityName() {
		int pageSize = 2;
		int pageNum = 0;
		String sortField = "cityName";
		String regionName = "Maharashtra"; 
		
		Sort sort = Sort.by(sortField).ascending();
		Pageable pageable = PageRequest.of(pageNum, pageSize, sort);
		
		Map<String,Object> filterFields = new HashMap<>();
		filterFields.put("regionName",regionName);
		
		Page<Location> pages = locationRepository.listWithFilter(pageable,Collections.emptyMap());
		List<Location> listLocations = pages.getContent();
		
		listLocations.forEach(System.out::println);
		
		assertThat(listLocations.size()).isEqualTo(pages.getSize());
		assertThat(listLocations).isSortedAccordingTo(new Comparator<Location>() {

			@Override
			public int compare(Location o1, Location o2) {
				// TODO Auto-generated method stub
				return o1.getCityName().compareTo(o2.getCityName());
			}
		});
	}
	
	
	@Test
	public void testListWithFilterByRegionNameSortedByCityName() {
		int pageSize = 2;
		int pageNum = 0;
		String sortField = "cityName";
		String regionName = "Maharashtra"; 
		
		Sort sort = Sort.by(sortField).ascending();
		Pageable pageable = PageRequest.of(pageNum, pageSize, sort);
		
		Map<String,Object> filterFields = new HashMap<>();
		filterFields.put("regionName",regionName);
		
		Page<Location> pages = locationRepository.listWithFilter(pageable,filterFields);
		List<Location> listLocations = pages.getContent();
		
		listLocations.forEach(System.out::println);
		
		assertThat(listLocations.size()).isEqualTo(pages.getSize());
		assertThat(listLocations).isSortedAccordingTo(new Comparator<Location>() {

			@Override
			public int compare(Location o1, Location o2) {
				// TODO Auto-generated method stub
				return o1.getCityName().compareTo(o2.getCityName());
			}
		});
	}
	
	@Test
	public void testListWithFilterByCountryCodeSortedByCode() {
		int pageSize = 2;
		int pageNum = 0;
		String sortField = "cityName";
		String countryCode = "IN"; 
		
		Sort sort = Sort.by(sortField).ascending();
		Pageable pageable = PageRequest.of(pageNum, pageSize, sort);
		
		Map<String,Object> filterFields = new HashMap<>();
		filterFields.put("countryCode",countryCode);
		
		Page<Location> pages = locationRepository.listWithFilter(pageable,filterFields);
		List<Location> listLocations = pages.getContent();
		
		listLocations.forEach(System.out::println);
		
		assertThat(listLocations).size().isEqualTo(pageSize);
		assertThat(listLocations).isSortedAccordingTo(new Comparator<Location>() {

			@Override
			public int compare(Location o1, Location o2) {
				// TODO Auto-generated method stub
				return o1.getCityName().compareTo(o2.getCityName());
			}
		});
	}
	
	@Test
	public void testListWithFilterByCountryCodeAndEnabledSortedByCityName() {
		int pageSize = 2;
		int pageNum = 0;
		String sortField = "cityName";
		String countryCode = "IN"; 
		boolean enabled = true;
		
		Sort sort = Sort.by(sortField).ascending();
		Pageable pageable = PageRequest.of(pageNum, pageSize, sort);
		
		Map<String,Object> filterFields = new HashMap<>();
		filterFields.put("countryCode",countryCode);
		filterFields.put("enabled",enabled);
		Page<Location> pages = locationRepository.listWithFilter(pageable,filterFields);
		List<Location> listLocations = pages.getContent();
		
		listLocations.forEach(System.out::println);
		
		assertThat(listLocations).size().isEqualTo(pageSize);
		assertThat(listLocations).isSortedAccordingTo(new Comparator<Location>() {

			@Override
			public int compare(Location o1, Location o2) {
				// TODO Auto-generated method stub
				return o1.getCityName().compareTo(o2.getCityName());
			}
		});
	}
}
