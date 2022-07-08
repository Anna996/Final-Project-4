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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.entities.ErrorMessage;
import ajbc.doodle.calendar.entities.Event;
import ajbc.doodle.calendar.entities.Notification;
import ajbc.doodle.calendar.entities.User;
import ajbc.doodle.calendar.entities.UserType;
import ajbc.doodle.calendar.services.EventService;
import ajbc.doodle.calendar.services.NotificationService;
import ajbc.doodle.calendar.services.UserService;

@RestController
@RequestMapping("/events")
public class EventController {

	@Autowired
	private EventService eventService;

	@Autowired
	private UserService userService;

//	@Autowired
//	private NotificationService notificationService;

//	@Autowired
//	private UserEventService userEventService;

	/**
	 * GET operations
	 * 
	 */

	@GetMapping
	public ResponseEntity<?> getAllEvents() {

		try {
			List<Event> events = eventService.getAllEvents();
			return ResponseEntity.ok(events);

		} catch (DaoException e) {
			ErrorMessage eMessage = ErrorMessage.getErrorMessage(e.getMessage(), "try again later...");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(eMessage);
		}
	}

//	@GetMapping("/user/{id}")
//	public ResponseEntity<?> getEventsByUserId(@PathVariable("id") int userId){
//		
//		try {
//			List<Event> events = eventService.getAllEvents();
//			return ResponseEntity.ok(events);
//			
//		} catch (DaoException e) {
//			ErrorMessage eMessage =  ErrorMessage.getErrorMessage(e.getMessage(), "try again later...");
//			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(eMessage);
//		}
//	}
	

	/**
	 * POST operations
	 * 
	 */

	@PostMapping
	public ResponseEntity<?> addEvent(@RequestBody Event event, @RequestParam(required = true) int userId) {

		try {
			eventService.addEvent(event, userId);
			Event fromDB = eventService.getEventById(event.getId());
			return ResponseEntity.status(HttpStatus.CREATED).body(fromDB);

		} catch (DaoException e) {
			ErrorMessage eMessage = ErrorMessage.getErrorMessage(e.getMessage(), "failed to create this event");
			return ResponseEntity.status(500).body(eMessage);
		}
	}

	private String getEventIdMessage(int id) {
		return "event id: " + id;
	}

	/**
	 * PUT operations
	 * 
	 */
	
	@PutMapping("/{id}/guests")
	public ResponseEntity<?> addGuestsToEvent(@PathVariable("id") int eventId, @RequestParam(required = true) int userId, @RequestBody List<Integer> guestIds){
		
		try {
			eventService.addGuestsToEvent(eventId, userId, guestIds);
//			List<User> guests = userService.getUsersByIdList(guestsIds);
			return ResponseEntity.ok(guestIds);
		} catch (DaoException e) {
			ErrorMessage eMessage = ErrorMessage.getErrorMessage(e.getMessage(), "failed to send this event to these guests");
			return ResponseEntity.status(500).body(eMessage);
		}
	}

	/**
	 * DELETE operations
	 * 
	 */

}
