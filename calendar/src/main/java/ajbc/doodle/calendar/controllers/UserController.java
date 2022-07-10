package ajbc.doodle.calendar.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.entities.ErrorMessage;
import ajbc.doodle.calendar.entities.User;
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
	public ResponseEntity<?> getAllUsers(@RequestParam(required = false) String start, @RequestParam(required = false) String end) {
		List<User> users;

		try {
			if (start != null && end != null) {
				users = userService.getUsersWithEventInRange(start, end);
			} else {
				users = userService.getAllUsers();
			}
			
			return ResponseEntity.ok(userService.filterByUserNotifications(users));

		} catch (DaoException e) {
			ErrorMessage eMessage = ErrorMessage.getErrorMessage(e.getMessage(), "fetching data failed");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(eMessage);
		}
	}

	@GetMapping("/{idOrEmail}")
	public ResponseEntity<?> getUserByIdOrEmail(@PathVariable String idOrEmail) {

		try {
			int id = Integer.valueOf(idOrEmail);
			return getUserById(id);
		} catch (NumberFormatException e) {
			return getUserByEmail(idOrEmail);
		}
	}

	private ResponseEntity<?> getUserById(int id) {

		try {
			User user = userService.getUserById(id);
			return ResponseEntity.ok(userService.filterByUserNotifications(user));

		} catch (DaoException e) {
			ErrorMessage eMessage = ErrorMessage.getErrorMessage(e.getMessage(), getUserIdMessage(id));
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(eMessage);
		}
	}

	private ResponseEntity<?> getUserByEmail(String email) {
		try {
			User user = userService.getUserByEmail(email);
			return ResponseEntity.ok(userService.filterByUserNotifications(user));

		} catch (DaoException e) {
			ErrorMessage eMessage = ErrorMessage.getErrorMessage(e.getMessage(), "user email: " + email);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(eMessage);
		}
	}

	// Get all users of an event by event id
	@GetMapping("/event/{id}")
	public ResponseEntity<?> getUsersByEventId(@PathVariable("id") int eventId) {
		try {
			List<User> users = userService.getUsersByEventId(eventId);
			return ResponseEntity.ok(userService.filterByUserNotifications(users));

		} catch (DaoException e) {
			ErrorMessage eMessage = ErrorMessage.getErrorMessage(e.getMessage(), "fetching data failed");
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
	public ResponseEntity<?> addUsers(@RequestBody List<User> users) {

		if (users == null || users.size() == 0) {
			ErrorMessage eMessage = ErrorMessage.getErrorMessage("didn't get user info", "failed to create user");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(eMessage);
		}

		if (users.size() == 1) {
			return addOneUser(users.get(0));
		}

		return addListUsers(users);
	}

	private ResponseEntity<?> addOneUser(User user) {
		try {
			userService.addUser(user);
			User fromDB = userService.getUserById(user.getId());
			return ResponseEntity.status(HttpStatus.CREATED).body(fromDB);

		} catch (DaoException e) {
			ErrorMessage eMessage = ErrorMessage.getErrorMessage(e.getMessage(), "failed to create this user");
			return ResponseEntity.status(500).body(eMessage);
		}
	}

	private ResponseEntity<?> addListUsers(List<User> users) {
		try {
			userService.addUsers(users);
			users = userService.getUsersByIds(users.stream().map(user -> user.getId()).collect(Collectors.toList()));
			return ResponseEntity.status(HttpStatus.CREATED).body(users);

		} catch (DaoException e) {
			ErrorMessage eMessage = ErrorMessage.getErrorMessage(e.getMessage(), "failed to create these users");
			return ResponseEntity.status(500).body(eMessage);
		}
	}

	/**
	 * PUT operations
	 * 
	 */
	
	@PutMapping
	public ResponseEntity<?> updateUser(@RequestBody List<User> users){

		if (users == null || users.size() == 0) {
			ErrorMessage eMessage = ErrorMessage.getErrorMessage("didn't get user info", "failed to update user");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(eMessage);
		}

		if (users.size() == 1) {
			return updateOneUser(users.get(0));
		}

		return updateUserList(users);
	}
	
	public ResponseEntity<?> updateOneUser( User user){
		try {
			userService.updateUser(user);
			User fromDB = userService.getUserById(user.getId());
			return ResponseEntity.ok(userService.filterByUserNotifications(fromDB));
		} catch (DaoException e) {
			ErrorMessage eMessage = ErrorMessage.getErrorMessage(e.getMessage(), getUserIdMessage(user.getId()));
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(eMessage);
		}
	}
	
	public ResponseEntity<?> updateUserList( List<User> users){
		try {
			userService.updateUsers(users);
			users = userService.getUsersByIds(users.stream().map(user -> user.getId()).collect(Collectors.toList()));
			return ResponseEntity.ok(userService.filterByUserNotifications(users));
		} catch (DaoException e) {
			ErrorMessage eMessage = ErrorMessage.getErrorMessage(e.getMessage(), "Failed to update these users");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(eMessage);
		}
	}

//	@PutMapping("/{id}/login/{email}")
//	public ResponseEntity<?> login(@PathVariable int id, @PathVariable String email) {
//		return updateIsLoggedIn(id, email, true);
//	}
//
//	@PutMapping("/{id}/logout/{email}")
//	public ResponseEntity<?> logout(@PathVariable int id, @PathVariable String email) {
//		return updateIsLoggedIn(id, email, false);
//	}
//
//	private ResponseEntity<?> updateIsLoggedIn(int id, String email, boolean isLoggedIn) {
//		try {
//
//			User user = userService.getUserById(id);
//
//			if (!user.getEmail().equals(email)) {
//				throw new DaoException("wrong email");
//			}
//
//			if (user.isLoggedIn() == isLoggedIn) {
//				throw new DaoException("you already logged " + (isLoggedIn ? "in :)" : "out"));
//			}
//
//			user.setLoggedIn(isLoggedIn);
//			userService.updateUser(user);
//
//			User fromDB = userService.getUserById(user.getId());
//			return ResponseEntity.ok(fromDB);
//
//		} catch (DaoException e) {
//			ErrorMessage eMessage = ErrorMessage.getErrorMessage(e.getMessage(), getUserIdMessage(id));
//			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(eMessage);
//		}
//	}
	
	/**
	 * DELETE operations
	 * 
	 */

}
