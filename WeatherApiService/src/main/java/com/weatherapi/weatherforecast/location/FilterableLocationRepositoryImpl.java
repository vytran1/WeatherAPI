package com.weatherapi.weatherforecast.location;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.weatherapi.weatherforecast.common.Location;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Repository
public class FilterableLocationRepositoryImpl implements FilterableLocationRepository {
    
	@Autowired
	private EntityManager entityManager;
	
	@Override
	public Page<Location> listWithFilter(Pageable pageable, Map<String, Object> filterFields) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Location> entityQuery = builder.createQuery(Location.class);
		
		Root<Location> entityRoot = entityQuery.from(Location.class);
		entityQuery.select(builder.construct(Location.class,entityRoot.get("code"), 
				                                            entityRoot.get("cityName"), 
				                                            entityRoot.get("regionName"),
				                                            entityRoot.get("countryName"),
				                                            entityRoot.get("countryCode"),
				                                            entityRoot.get("enabled")
				));
		
		Predicate[] predicates = createPredicate(filterFields, builder,entityRoot);
		if(predicates.length > 0) {
			entityQuery.where(predicates);
		}
		
		
		
		List<Order> orders = new ArrayList<>();
		pageable.getSort().stream().forEach(order -> {
			System.out.println("Order Field: " + order.getProperty());
			if(order.isAscending()) {
				orders.add(builder.asc(entityRoot.get(order.getProperty())));
			}else {
				orders.add(builder.desc(entityRoot.get(order.getProperty())));
			}
		});
		
		entityQuery.orderBy(orders);
		
		
		TypedQuery<Location> typeQuery = entityManager.createQuery(entityQuery);
		typeQuery.setFirstResult((int) pageable.getOffset());
		typeQuery.setMaxResults(pageable.getPageSize());
		
		List<Location> listLocations = typeQuery.getResultList();
		
		// TODO Auto-generated method stub
		//List<Location> listLocations = new ArrayList<>();
		
		Long totalRows = getTotalRowCountr(filterFields);
		
		return new PageImpl<>(listLocations, pageable,totalRows);
	}

	private Predicate[] createPredicate(Map<String, Object> filterFields, CriteriaBuilder builder, Root<Location> root) {
		//forgot to add criteria of trashed -> adding
		Predicate[] predicates = new Predicate[filterFields.size() + 1];
		if(!filterFields.isEmpty()) {
			
			Iterator<String> iterator = filterFields.keySet().iterator();
			
			int i =0;
			
			while(iterator.hasNext()) {
				String fieldName = iterator.next();
				Object fieldValue = filterFields.get(fieldName);
				
				System.out.println("Field Name: " + fieldName + " Field Value: " + fieldValue);
				
				predicates[i++] = builder.equal(root.get(fieldName),fieldValue);
			}
			
		}
		predicates[predicates.length -1] = builder.equal(root.get("trashed"),false);
		return predicates;
	}
    
	private Long getTotalRowCountr(Map<String, Object> filterFields) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
		
		Root countRoot = countQuery.from(Location.class);
		
		countQuery.select(builder.count(countRoot));
		Predicate[] predicates = createPredicate(filterFields, builder,countRoot);
		
		if(predicates.length > 0) {
			countQuery.where(predicates);
		}
		
		Long totalRowCount  = entityManager.createQuery(countQuery).getSingleResult();
		return totalRowCount;
	}
	
}
