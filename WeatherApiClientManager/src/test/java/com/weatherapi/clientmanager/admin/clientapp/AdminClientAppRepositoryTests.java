package com.weatherapi.clientmanager.admin.clientapp;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.weatherapi.weatherforecast.common.ClientApp;
import com.weatherapi.weatherforecast.common.Location;
import com.weatherapi.weatherforecast.common.User;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class AdminClientAppRepositoryTests {
	@Autowired 
	private AdminClientAppRepository repo;
	
	@Test
	public void testAddSystemApp() {
		User userAdmin = new User(1);
		
		ClientApp newApp = ClientApp.newSystemApp();
		newApp.setClientId("Client-ID-XXXX");
		newApp.setClientSecret("Client-Secret-YYYYY");
		newApp.setName("Default System App");
		newApp.setEnabled(true);
		newApp.setUser(userAdmin);
		
		ClientApp savedApp = repo.save(newApp);
		assertThat(savedApp).isNotNull();
		assertThat(savedApp.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testAddReaderApp() {
		User userNam = new User(2);
		
		ClientApp newApp = ClientApp.newReaderApp();
		newApp.setClientId("Client-ID-ABCDEFG");
		newApp.setClientSecret("Client-Secret-HJJKLLQ");
		newApp.setName("Reader App #1");
		newApp.setEnabled(false);
		newApp.setUser(userNam);
		
		ClientApp savedApp = repo.save(newApp);
		assertThat(savedApp).isNotNull();
		assertThat(savedApp.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testAddUpdaterApp() {
		User userTom = new User(10);
		Location location = new Location("DANA_VN");
		
		ClientApp newApp = ClientApp.newUpdaterApp();
		newApp.setClientId("Client-ID-OQIONGNL1NBG");
		newApp.setClientSecret("Client-Secret-ZNQOIn08N1ON");
		newApp.setName("Updater App #2");
		newApp.setEnabled(true);
		newApp.setUser(userTom);
		newApp.setLocation(location);
		
		ClientApp savedApp = repo.save(newApp);
		assertThat(savedApp).isNotNull();
		assertThat(savedApp.getId()).isGreaterThan(0);
	}	
}
