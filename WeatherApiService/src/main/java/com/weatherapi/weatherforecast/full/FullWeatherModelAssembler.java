package com.weatherapi.weatherforecast.full;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.weatherapi.weatherforecast.GeoLocationException;
import com.weatherapi.weatherforecast.daily.DailyWeatherController;
import com.weatherapi.weatherforecast.daily.DailyWeatherListDTO;


@Component
public class FullWeatherModelAssembler implements RepresentationModelAssembler<FullWeatherDTO,EntityModel<FullWeatherDTO>> {

	@Override
	public EntityModel<FullWeatherDTO> toModel(FullWeatherDTO entity) {
		EntityModel<FullWeatherDTO> entityModel = EntityModel.of(entity);
		try {
			entityModel.add(
					linkTo(
							methodOn(FullWeatherController.class).getFullWeatherByIPAddress(null))
					                 .withSelfRel());
		} catch (GeoLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return entityModel;
	}
   
	
	
}
