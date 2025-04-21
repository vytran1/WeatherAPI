package com.weatherapi.weatherforecast.location;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.weatherapi.weatherforecast.common.Location;

public interface LocationRepository extends CrudRepository<Location,String>,PagingAndSortingRepository<Location,String>,FilterableLocationRepository {
   
	@Query("SELECT l FROM Location l WHERE l.trashed = false")
	@Deprecated
	public List<Location> findUntrashed();
	
	
	@Query("SELECT l FROM Location l WHERE l.trashed = false")
	@Deprecated
	public Page<Location> findByPageAndUnTrashed(Pageable pageable);
	
	@Query("SELECT l FROM Location l WHERE l.code = ?1 AND l.trashed = false")
	public Location findByCode(String code);
	
	
	@Query("UPDATE Location l SET l.trashed = true WHERE l.code = ?1")
	@Modifying
	public void trashByCode(String code);
	
	@Query("SELECT l FROM Location l WHERE l.countryCode = ?1 AND l.cityName = ?2 AND l.trashed = false")
	public Location findByCountryCodeAndCityName(String countryCode,String cityName);
}
