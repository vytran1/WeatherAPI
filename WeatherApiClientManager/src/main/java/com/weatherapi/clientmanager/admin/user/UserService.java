package com.weatherapi.clientmanager.admin.user;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.weatherapi.weatherforecast.common.User;
import com.weatherapi.weatherforecast.common.UserType;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserService {
		
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	public static final Integer USERS_PER_PAGE = 3;
	
	public Page<User> listByPage(int pageNum,String sortField,String sortDir,String keyword){
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		
		Pageable pageable = PageRequest.of(pageNum - 1,USERS_PER_PAGE,sort);
		
		if(keyword != null) {
			return userRepository.findAll(keyword, pageable);
		}
			
		return userRepository.findAll(pageable);
	}
	
	
	public void save(User user) throws UserEmailNotUniqueException {
		
		boolean isEmailUnique = isEmailUnique(user);
		
		if(!isEmailUnique) {
			throw new UserEmailNotUniqueException("Email is used by other people in the system");
		}
		
		boolean isEditing = user.getId() != null;
		
		if(isEditing && user.getPassword().isEmpty()) {
			User userInDB = userRepository.findById(user.getId()).get();
			user.setPassword(userInDB.getPassword());
		}else {
			encodePassword(user);
		}
		
		userRepository.save(user);
	}
	
	public User get(Integer id) throws UserNotFoundException {
		Optional<User> optional = userRepository.findById(id);
		
		if(optional.isEmpty()) {
			throw new UserNotFoundException("Not Found User With The Given Id " + id);
		}
		
		return optional.get();
		
	}
	
	public void delete(Integer id) throws UserNotFoundException{
		Optional<User> optional = userRepository.findById(id);
		
		if(optional.isEmpty()) {
			throw new UserNotFoundException("Not Found User With The Given Id " + id);
		}
		
		userRepository.trashById(id);
	}
	
	public void updateUserEnabledStatus(Integer id, Boolean enabled) throws UserNotFoundException {
		Optional<User> optional = userRepository.findById(id);
		
		if(optional.isEmpty()) {
			throw new UserNotFoundException("Not Found User With The Given Id " + id);
		}
		
		userRepository.updateEnabledStatus(id, false);
	}
	
	public void addClientUser(User user) throws UserEmailNotUniqueException {
		if (!isEmailUnique(user)) {
			throw new UserEmailNotUniqueException("The email " + user.getEmail() + " is already taken");
		}
		
		user.setType(UserType.CLIENT);
		user.setEnabled(false);
		encodePassword(user);
		
		userRepository.save(user);
	}
	
	public void updateAccount(User user, String name, String newPassword) {
		user.setName(name);
		
		if (!newPassword.isEmpty()) {
			user.setPassword(newPassword);
			encodePassword(user);
		}
		
		userRepository.save(user);
	}
	
	
	private boolean isEmailUnique(User userInForm) {
		User userByEmail = userRepository.findByEmail(userInForm.getEmail());
		if(userByEmail != null && !userByEmail.getId().equals(userInForm.getId())) {
			return false;
		}
		return true;
	}
	
	void encodePassword(User user) {
		String encodedPassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodedPassword);
	}
	
	public List<User> searchAutoComplete(String keyword) {
		return userRepository.search(keyword);
	}
}
