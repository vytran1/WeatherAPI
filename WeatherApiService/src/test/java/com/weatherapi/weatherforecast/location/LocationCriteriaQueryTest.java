package com.weatherapi.weatherforecast.location;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.weatherapi.weatherforecast.common.Location;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE )
public class LocationCriteriaQueryTest {
   
	@Autowired
	private EntityManager entityManager;
	
	@Test
	public void testCriteriaQuery() {
	   CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
	   CriteriaQuery<Location> creCriteriaQuery = criteriaBuilder.createQuery(Location.class);
	   
	   Root<Location> rootObject =  creCriteriaQuery.from(Location.class);
	   
	   //add where clause
	   //Predicate predicate = criteriaBuilder.equal(rootObject.get("countryCode"),"US");
	   //creCriteriaQuery.where(predicate);
	   
	   //add Order 
	   creCriteriaQuery.orderBy(criteriaBuilder.asc(rootObject.get("cityName")));
	   
	   
	   
	   
	   TypedQuery<Location> typeQuery = entityManager.createQuery(creCriteriaQuery);
	   //add pagination
	   typeQuery.setFirstResult(0);
	   typeQuery.setMaxResults(2);
	   List<Location> listLocations = typeQuery.getResultList();
	   
	   assertThat(listLocations).isNotEmpty();
	   listLocations.forEach(System.out::println);
	}
	
	@Test
	public void testJPQL() {
		String jpql = "FROM Location WHERE countryCode = 'US'";

		TypedQuery<Location> typeQuery = entityManager.createQuery(jpql, Location.class);

		List<Location> listLocations = typeQuery.getResultList();

		assertThat(listLocations).isNotEmpty();
		listLocations.forEach(System.out::println);
	}
}
