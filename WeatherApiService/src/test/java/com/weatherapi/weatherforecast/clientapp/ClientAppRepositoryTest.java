package com.weatherapi.weatherforecast.clientapp;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.weatherapi.weatherforecast.common.ClientApp;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class ClientAppRepositoryTest {
		
	
	
	@Autowired
	private ClientAppRepository clientAppRepository;
	
	
	@Test
	public void testFindByClientIdNotFound() {
		String clientId = "adadadad";
		Optional<ClientApp> clientAppOPT = clientAppRepository.findByClientId(clientId);
		
		
		assertThat(clientAppOPT).isNotPresent();
		
		
	}
	
	@Test
	public void testFindByClientIdFound() {
		String clientId = "lQZhpQE6br0ipC8o3fVX";
        Optional<ClientApp> clientAppOPT = clientAppRepository.findByClientId(clientId);
		
		
		assertThat(clientAppOPT).isPresent();
	}

}
