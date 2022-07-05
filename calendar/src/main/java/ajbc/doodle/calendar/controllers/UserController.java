package ajbc.doodle.calendar.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.entities.ErrorMessage;
import ajbc.doodle.calendar.entities.User;
import ajbc.doodle.calendar.entities.UserEvent;
import ajbc.doodle.calendar.services.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

	@Autowired
	private UserService userService; 
	
	/**
	 * GET operations
	 * 
	 */
	
	@GetMapping
	public ResponseEntity<?> getAllUsers(){
		
		try {
			List<User> users = userService.getAllUsers();
			return ResponseEntity.ok(users);
			
		} catch (DaoException e) {
			ErrorMessage eMessage =  ErrorMessage.getErrorMessage(e.getMessage(), "try again later...");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(eMessage);
		}
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<?> getUserById(@PathVariable int id){
		
		try {
			User user = userService.getUserById(id);
			return ResponseEntity.ok(user);
			
		} catch (DaoException e) {
			ErrorMessage eMessage =  ErrorMessage.getErrorMessage(e.getMessage(), getUserIdMessage(id));
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(eMessage);
		}
	}
	
	private String getUserIdMessage(int id) {
		return "user id: " + id;
	}
	
	
	
	/**
	 * POST operations
	 * 
	 */
	
	@PostMapping
	public ResponseEntity<?> createUser(@RequestBody User user){
		try {
			userService.addUser(user);
			User fromDB = userService.getUserById(user.getId());
			return ResponseEntity.status(HttpStatus.CREATED).body(fromDB);	
			
		} catch (DaoException e) {
			ErrorMessage eMessage =  ErrorMessage.getErrorMessage(e.getMessage(), getUserIdMessage(user.getId()));
			return ResponseEntity.status(500).body(eMessage);
		}
	}
	
	
	/**
	 * PUT operations
	 * 
	 */
	
	@PutMapping("/{id}/login/{email}")
	public ResponseEntity<?> login(@PathVariable int id, @PathVariable String email){
		return updateIsLoggedIn(id, email, true);
	}
	
	@PutMapping("/{id}/logout/{email}")
	public ResponseEntity<?> logout(@PathVariable int id, @PathVariable String email){
		return updateIsLoggedIn(id, email, false);
	}
	
	private ResponseEntity<?> updateIsLoggedIn(int id, String email, boolean isLoggedIn){
		try {
			
			User user = userService.getUserById(id);
			
			if(!user.getEmail().equals(email)) {
				throw new DaoException("wrong email");
			}

			if(user.isLoggedIn() == isLoggedIn) {
				throw new DaoException("you already logged " + (isLoggedIn ? "in :)" : "out"));
			}
			
			user.setLoggedIn(isLoggedIn);
			userService.updateUser(user);
		
			User fromDB = userService.getUserById(user.getId());
			return ResponseEntity.ok(fromDB);	
			
		} catch (DaoException e) {
			ErrorMessage eMessage =  ErrorMessage.getErrorMessage(e.getMessage(), getUserIdMessage(id));
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(eMessage);
		}
	}
	
	/**
	 * DELETE operations
	 * 
	 */
	

	
	
	/**
	 * UserEvent operations
	 * 
	 */
	
}
