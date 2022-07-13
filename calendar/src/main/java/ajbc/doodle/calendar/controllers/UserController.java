package ajbc.doodle.calendar.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import ajbc.doodle.calendar.entities.SubscriptionData;
import ajbc.doodle.calendar.entities.User;
import ajbc.doodle.calendar.services.SubscriptionDataService;
import ajbc.doodle.calendar.services.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private SubscriptionDataService dataService;

	/**
	 * GET operations
	 * 
	 */

	@GetMapping
	public ResponseEntity<?> getAllUsers(@RequestParam(required = false) String start,
			@RequestParam(required = false) String end) {
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

	@GetMapping("/data")
	public ResponseEntity<?> getData() {
		try {
			List<SubscriptionData> subscriptions = dataService.getAllSubscriptions();
			return ResponseEntity.ok(subscriptions);
		} catch (DaoException e) {
			ErrorMessage eMessage = ErrorMessage.getErrorMessage(e.getMessage(), "Failed to fetch subscriptions data");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(eMessage);
		}
	}

	/**
	 * POST operations
	 * 
	 */

	@PostMapping
	public ResponseEntity<?> addUsers(@RequestBody List<User> users) {

		if (users == null || users.size() == 0) {
			ErrorMessage eMessage = ErrorMessage.getErrorMessage("didn't get users info", "failed to create users");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(eMessage);
		}

		try {
			userService.addUsers(users);
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
	public ResponseEntity<?> updateUsers(@RequestBody List<User> users) {

		if (users == null || users.size() == 0) {
			ErrorMessage eMessage = ErrorMessage.getErrorMessage("didn't get users info", "failed to update users");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(eMessage);
		}

		try {
			userService.updateUsers(users);
			return ResponseEntity.ok(userService.filterByUserNotifications(users));
		} catch (DaoException e) {
			ErrorMessage eMessage = ErrorMessage.getErrorMessage(e.getMessage(), "Failed to update these users");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(eMessage);
		}
	}

	/**
	 * DELETE operations
	 * 
	 */

	@DeleteMapping("/{id}")
	public ResponseEntity<?> softDeleteUser(@PathVariable int id) {
		try {
			userService.softDeleteUser(id);
			User user = userService.getUserById(id);
			return ResponseEntity.ok(userService.filterByUserNotifications(user));

		} catch (DaoException e) {
			ErrorMessage eMessage = ErrorMessage.getErrorMessage(e.getMessage(), "Failed to delete this user");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(eMessage);
		}
	}

	@DeleteMapping("{id}/delete")
	public ResponseEntity<?> hardDeleteUser(@PathVariable int id) {
		try {
			User user = userService.getUserById(id);
			userService.hardDeleteUser(id);
			return ResponseEntity.ok(userService.filterByUserNotifications(user));

		} catch (DaoException e) {
			ErrorMessage eMessage = ErrorMessage.getErrorMessage(e.getMessage(), "Failed to hard delete this user");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(eMessage);
		}
	}
}
