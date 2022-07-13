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

/**
 * Restful api service that receives http requests about Users of the calendar.
 * 
 * @author Anna Aba
 *
 */
@RestController
@RequestMapping("/users")
public class UserController {

	@Autowired
	private UserService userService;

	/**
	 * GET operations
	 * 
	 */

	/**
	 * Returns all users (not active also) from the database. 
	 * Two optional params - start and end date-time of event. 
	 * If both parameters exist - then the function returns all users with event in this range.
	 * 
	 * @param start date-time of event.
	 * @param end   date-time of event.
	 * @return ResponseEntity with list of users.
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

	/**
	 * Gets an unique id or email, and returns the user with it.
	 * 
	 * @param idOrEmail String that represents an id of user or an email.
	 * @return ResponseEntity with the user.
	 */
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

	/**
	 * Returns all the users that have this event.
	 * 
	 * @param eventId the id of the event.
	 * @return ResponseEntity with list of users.
	 */
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

	/**
	 * Adds list of new users with unique email to the database.
	 * @param users list of new users.
	 * @return ResponseEntity with the list of new users.
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

	/**
	 * Updates list of existed users in the database.
	 * @param users list of users to update.
	 * @return ResponseEntity with the list of updated users.
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

	/**
	 * Deletes user by switching the isActive flag to false.
	 * @param id the id of the user to delete.
	 * @return ResponseEntity with the deleted user.
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

	/**
	 * Deletes the user completely from the database, and his notifications also.
	 * @param id the id of the user to delete.
	 * @return ResponseEntity with the deleted user.
	 */
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
