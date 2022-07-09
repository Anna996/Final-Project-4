package ajbc.doodle.calendar.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.entities.ErrorMessage;
import ajbc.doodle.calendar.entities.Notification;
import ajbc.doodle.calendar.services.NotificationService;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

	@Autowired
	private NotificationService notificationService;

//	@Autowired
//	private UserEventService userEventService;

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
			return ResponseEntity.status(HttpStatus.CREATED).body(notifications);

		} catch (DaoException e) {
			ErrorMessage eMessage = ErrorMessage.getErrorMessage(e.getMessage(), "failed to create these notifications");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(eMessage);
		}
	}

	/**
	 * PUT operations
	 * 
	 */

	/**
	 * DELETE operations
	 * 
	 */

}
