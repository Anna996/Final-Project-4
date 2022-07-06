package ajbc.doodle.calendar.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.entities.ErrorMessage;
import ajbc.doodle.calendar.entities.Notification;
import ajbc.doodle.calendar.services.NotificationService;
import ajbc.doodle.calendar.services.UserEventService;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

	@Autowired
	private NotificationService notificationService; 
	
	@Autowired
	private UserEventService userEventService;
	
	/**
	 * GET operations
	 * 
	 */
	
	@GetMapping
	public ResponseEntity<?> getAllNotifications(){
		
		try {
			List<Notification> notifications = notificationService.getAllNotifications();
			return ResponseEntity.ok(notifications);
			
		} catch (DaoException e) {
			ErrorMessage eMessage =  ErrorMessage.getErrorMessage(e.getMessage(), "try again later...");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(eMessage);
		}
	}
	
	
	/**
	 * POST operations
	 * 
	 */
	
	@PostMapping
	public ResponseEntity<?> addNotification(@RequestBody Notification notification){
		
		int eventId = notification.getEventId();
		int userId = notification.getUserId();
		
		// check if event and user exist in UserEvent Table
		try {
			userEventService.getUserEventByIDs(userId, eventId);
		} catch (DaoException e) {
			ErrorMessage eMessage =  ErrorMessage.getErrorMessage(e.getMessage(), "userId: %s , eventId: %s".formatted(userId, eventId));
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(eMessage);
		}
		
		// if true - add to DB
		try {
			notificationService.addNotification(notification);
			Notification fromDB = notificationService.getNotificationById(notification.getId());
			return ResponseEntity.status(HttpStatus.CREATED).body(fromDB);
			
		} catch (DaoException e) {
			ErrorMessage eMessage =  ErrorMessage.getErrorMessage(e.getMessage(), "notification id: " + notification.getId());
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
