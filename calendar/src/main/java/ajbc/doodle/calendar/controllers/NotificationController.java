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

	@PostMapping
	public ResponseEntity<?> addNotification(@RequestBody List<Notification> notifications) {

		if (notifications == null || notifications.size() == 0) {
			ErrorMessage eMessage = ErrorMessage.getErrorMessage("didn't get notification info",
					"failed to create notification");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(eMessage);
		}

		if (notifications.size() == 1) {
			return addOneNotification(notifications.get(0));
		}

		return addListNotifications(notifications);
	}

	public ResponseEntity<?> addOneNotification(Notification notification) {
		try {
			notificationService.addNotification(notification);
			Notification fromDB = notificationService.getNotificationById(notification.getId());
			manager.addNotification(fromDB);
			return ResponseEntity.status(HttpStatus.CREATED).body(fromDB);

		} catch (DaoException e) {
			ErrorMessage eMessage = ErrorMessage.getErrorMessage(e.getMessage(), "failed to create this notification");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(eMessage);
		}
	}

	public ResponseEntity<?> addListNotifications(@RequestBody List<Notification> notifications) {
		try {
			notificationService.addNotifications(notifications);
			notifications = notificationService.getNotificationsByIds(
					notifications.stream().map(notification -> notification.getId()).collect(Collectors.toList()));
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

	@PutMapping
	public ResponseEntity<?> updateNotification(@RequestBody List<Notification> notifications) {

		if (notifications == null || notifications.size() == 0) {
			ErrorMessage eMessage = ErrorMessage.getErrorMessage("didn't get notification info",
					"failed to update notification");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(eMessage);
		}

		if (notifications.size() == 1) {
			return updateOneNotification(notifications.get(0));
		}

		return updateListNotifications(notifications);
	}

	public ResponseEntity<?> updateOneNotification(Notification notification) {

		try {
			notificationService.updateNotification(notification);
			Notification fromDB = notificationService.getNotificationById(notification.getId());
			manager.updateNotification(fromDB);
			return ResponseEntity.status(HttpStatus.OK).body(fromDB);

		} catch (DaoException e) {
			ErrorMessage eMessage = ErrorMessage.getErrorMessage(e.getMessage(), "failed to update this notification");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(eMessage);
		}
	}

	public ResponseEntity<?> updateListNotifications(@RequestBody List<Notification> notifications) {
		try {
			notificationService.updateNotifications(notifications);
			notifications = notificationService.getNotificationsByIds(
					notifications.stream().map(notification -> notification.getId()).collect(Collectors.toList()));
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

	@DeleteMapping
	public ResponseEntity<?> softDeleteNotification(@RequestBody List<Integer> notificationIds) {

		if (notificationIds == null || notificationIds.size() == 0) {
			ErrorMessage eMessage = ErrorMessage.getErrorMessage("didn't get notification info",
					"failed to delete notification");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(eMessage);
		}

		if (notificationIds.size() == 1) {
			return softDeleteOneNotification(notificationIds.get(0));
		}

		return softDeleteNotifications(notificationIds);
	}

	public ResponseEntity<?> softDeleteOneNotification(int id) {

		try {
			notificationService.softDeleteNotification(id);
			Notification fromDB = notificationService.getNotificationById(id);
			return ResponseEntity.ok(fromDB);

		} catch (DaoException e) {
			ErrorMessage eMessage = ErrorMessage.getErrorMessage(e.getMessage(), "failed to delete this notification");
			return ResponseEntity.status(500).body(eMessage);
		}
	}

	public ResponseEntity<?> softDeleteNotifications(List<Integer> eventIds) {

		try {
			notificationService.softDeleteNotifications(eventIds);
			List<Notification> notifications = notificationService.getNotificationsByIds(eventIds);
			return ResponseEntity.ok(notifications);

		} catch (DaoException e) {
			ErrorMessage eMessage = ErrorMessage.getErrorMessage(e.getMessage(),
					"failed to delete these notifications");
			return ResponseEntity.status(500).body(eMessage);
		}
	}

	@DeleteMapping("/delete")
	public ResponseEntity<?> hardDeleteNotification(@RequestBody List<Integer> notificationIds) {

		if (notificationIds == null || notificationIds.size() == 0) {
			ErrorMessage eMessage = ErrorMessage.getErrorMessage("didn't get notification info",
					"failed to delete notification");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(eMessage);
		}

		if (notificationIds.size() == 1) {
			return hardDeleteOneNotification(notificationIds.get(0));
		}

		return hardDeleteNotifications(notificationIds);
	}

	public ResponseEntity<?> hardDeleteOneNotification(int id) {

		try {
			notificationService.hardDeleteNotification(id);
			Notification fromDB = notificationService.getNotificationById(id);
			return ResponseEntity.ok(fromDB);

		} catch (DaoException e) {
			ErrorMessage eMessage = ErrorMessage.getErrorMessage(e.getMessage(), "failed to delete this notification");
			return ResponseEntity.status(500).body(eMessage);
		}
	}

	public ResponseEntity<?> hardDeleteNotifications(List<Integer> eventIds) {

		try {
			notificationService.hardDeleteNotifications(eventIds);
			List<Notification> notifications = notificationService.getNotificationsByIds(eventIds);
			return ResponseEntity.ok(notifications);

		} catch (DaoException e) {
			ErrorMessage eMessage = ErrorMessage.getErrorMessage(e.getMessage(),
					"failed to delete these notifications");
			return ResponseEntity.status(500).body(eMessage);
		}
	}
}
