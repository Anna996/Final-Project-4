package ajbc.doodle.calendar.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
	
	// TODO: remove. 
	@GetMapping("/events")
	public ResponseEntity<?> getAllUserEvents(){
		
		try {
			List<UserEvent> users = userService.getAllUserEvents();
			return ResponseEntity.ok(users);
			
		} catch (DaoException e) {
			ErrorMessage eMessage =  ErrorMessage.getErrorMessage(e.getMessage(), "try again later...");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(eMessage);
		}
	}
}
