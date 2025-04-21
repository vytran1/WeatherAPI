package com.weatherapi.clientmanager.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;

import com.weatherapi.weatherforecast.common.User;
import com.weatherapi.weatherforecast.common.UserType;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class UserRepositoryTest {
	@Autowired 
	private UserRepository repo;
	
	@Test
	public void testAddAdminUser() {
		User user = new User();
		user.setName("Tran Nguyen Vy");
		user.setEmail("vytran@gmail.com");
		user.setType(UserType.ADMIN);

		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		user.setPassword(passwordEncoder.encode("nam2024"));
		
		User savedUser = repo.save(user);
		assertThat(savedUser).isNotNull();
	}
	
	@Test
	public void testAddClientUser() {
		User user = new User();
		user.setId(2);
		user.setName("Mike Murray");
		user.setEmail("mike.murray@gmail.com");
		user.setType(UserType.CLIENT);
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		user.setPassword(passwordEncoder.encode("mike123"));
		
		User savedUser = repo.save(user);
		assertThat(savedUser).isNotNull();
	}	
}
