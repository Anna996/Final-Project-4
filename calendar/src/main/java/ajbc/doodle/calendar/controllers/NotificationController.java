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
import ajbc.doodle.calendar.entities.Notification;
import ajbc.doodle.calendar.services.NotificationService;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

	@Autowired
	private NotificationService notificationService; 
	
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
	
}
