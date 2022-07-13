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
import org.springframework.web.bind.annotation.RestController;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.entities.ErrorMessage;
import ajbc.doodle.calendar.entities.Event;
import ajbc.doodle.calendar.entities.Notification;
import ajbc.doodle.calendar.entities.NotificationManager;
import ajbc.doodle.calendar.services.NotificationService;

/**
 * Restful api service that receives http requests about existed Notifications in the calendar.
 * @author Anna Aba
 *
 */
@RestController
@RequestMapping("/notifications")
public class NotificationController {

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private NotificationManager manager;

	/**
	 * GET operations
	 * 
	 */

	/**
	 * Returns all notifications (not active also) from the database. 
	 * @return ResponseEntity with list of all notifications.
	 */
	@GetMapping
	public ResponseEntity<?> getAllNotifications() {

		try {
			List<Notification> notifications = notificationService.getAllNotifications();
			return ResponseEntity.ok(notifications);

		} catch (DaoException e) {
			ErrorMessage eMessage = ErrorMessage.getErrorMessage(e.getMessage(), "fetching data failed");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(eMessage);
		}
	}
	/**
	 * Returns the notification with this id.
	 * @param id the id of the notification.
	 * @return ResponseEntity with the notification.
	 */

	@GetMapping("/{id}")
	public ResponseEntity<?> getNotificationById(@PathVariable int id) {

		try {
			Notification notification = notificationService.getNotificationById(id);
			return ResponseEntity.ok(notification);

		} catch (DaoException e) {
			ErrorMessage eMessage = ErrorMessage.getErrorMessage(e.getMessage(), "fetching data failed");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(eMessage);
		}
	}

	/**
	 * Returns all notifications of an event.
	 * @param eventId the id of the event.
	 * @return ResponseEntity with list of the notifications.
	 */
	@GetMapping("/event/{id}")
	public ResponseEntity<?> getNotificationsByEventId(@PathVariable("id") int eventId) {

		try {
			List<Notification> notifications = notificationService.getNotificationsByEventId(eventId);
			return ResponseEntity.ok(notifications);

		} catch (DaoException e) {
			ErrorMessage eMessage = ErrorMessage.getErrorMessage(e.getMessage(), "fetching data failed");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(eMessage);
		}
	}

	/**
	 * POST operations
	 * 
	 */

	/**
	 * Adds list of new notifications of an user and of event to the database.
	 * @param notifications list of new notifications.
	 * @return ResponseEntity with the list of new notifications.
	 */
	@PostMapping
	public ResponseEntity<?> addNotifications(@RequestBody List<Notification> notifications) {

		if (notifications == null || notifications.size() == 0) {
			ErrorMessage eMessage = ErrorMessage.getErrorMessage("didn't get notifications info",
					"failed to create notifications");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(eMessage);
		}

		try {
			notificationService.addNotifications(notifications);
			manager.addNotifications(notifications);
			return ResponseEntity.status(HttpStatus.CREATED).body(notifications);

		} catch (DaoException e) {
			ErrorMessage eMessage = ErrorMessage.getErrorMessage(e.getMessage(),
					"failed to create these notifications");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(eMessage);
		}
	}

	/**
	 * PUT operations
	 * 
	 */

	/**
	 * Updates list of existed notifications.
	 * @param notifications list of notifications to update.
	 * @return ResponseEntity with the list of updated notifications.
	 */
	@PutMapping
	public ResponseEntity<?> updateNotifications(@RequestBody List<Notification> notifications) {

		if (notifications == null || notifications.size() == 0) {
			ErrorMessage eMessage = ErrorMessage.getErrorMessage("didn't get notifications info",
					"failed to update notifications");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(eMessage);
		}

		try {
			notificationService.updateNotifications(notifications);
			manager.updateNotifications(notifications);
			return ResponseEntity.status(HttpStatus.OK).body(notifications);

		} catch (DaoException e) {
			ErrorMessage eMessage = ErrorMessage.getErrorMessage(e.getMessage(),
					"failed to update these notifications");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(eMessage);
		}
	}

	/**
	 * DELETE operations
	 * 
	 */

	/**
	 * Deletes notifications by switching their isActive flag to false.
	 * @param notificationIds list of ids of the notifications to delete.
	 * @return ResponseEntity with list of deleted notifications.
	 */
	@DeleteMapping
	public ResponseEntity<?> softDeleteNotifications(@RequestBody List<Integer> notificationIds) {

		if (notificationIds == null || notificationIds.size() == 0) {
			ErrorMessage eMessage = ErrorMessage.getErrorMessage("didn't get notifications info",
					"failed to delete notifications");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(eMessage);
		}

		try {
			notificationService.softDeleteNotifications(notificationIds);
			List<Notification> notifications = notificationService.getNotificationsByIds(notificationIds);
			return ResponseEntity.ok(notifications);

		} catch (DaoException e) {
			ErrorMessage eMessage = ErrorMessage.getErrorMessage(e.getMessage(),
					"failed to delete these notifications");
			return ResponseEntity.status(500).body(eMessage);
		}
	}

	/**
	 * Deletes notifications completely from the database.
	 * @param notificationIds list of ids of the notifications to delete.
	 * @return ResponseEntity with list of deleted notifications.
	 */
	@DeleteMapping("/delete")
	public ResponseEntity<?> hardDeleteNotifications(@RequestBody List<Integer> notificationIds) {

		if (notificationIds == null || notificationIds.size() == 0) {
			ErrorMessage eMessage = ErrorMessage.getErrorMessage("didn't get notifications info",
					"failed to delete notifications");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(eMessage);
		}

		try {
			List<Notification> notifications = notificationService.getNotificationsByIds(notificationIds);
			notificationService.hardDeleteNotifications(notificationIds);
			return ResponseEntity.ok(notifications);

		} catch (DaoException e) {
			ErrorMessage eMessage = ErrorMessage.getErrorMessage(e.getMessage(),
					"failed to delete these notifications");
			return ResponseEntity.status(500).body(eMessage);
		}
	}
}
