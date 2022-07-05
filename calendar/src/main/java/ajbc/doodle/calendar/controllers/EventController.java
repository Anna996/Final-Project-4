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
import ajbc.doodle.calendar.entities.Event;
import ajbc.doodle.calendar.entities.User;
import ajbc.doodle.calendar.entities.UserEvent;
import ajbc.doodle.calendar.entities.UserType;
import ajbc.doodle.calendar.services.EventService;
import ajbc.doodle.calendar.services.UserEventService;
import ajbc.doodle.calendar.services.UserService;

@RestController
@RequestMapping("/events")
public class EventController {

	@Autowired
	private EventService eventService; 
	
	@Autowired
	private UserService userService; 
	
	@Autowired
	private UserEventService userEventService;
	
	/**
	 * GET operations
	 * 
	 */
	
	@GetMapping
	public ResponseEntity<?> getAllEvents(){
		
		try {
			List<Event> events = eventService.getAllEvents();
			return ResponseEntity.ok(events);
			
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
	public ResponseEntity<?> addEvent(@RequestBody Event event, @RequestParam(required = true) int userId){
		
		try {
			User user =  userService.getUserById(userId);
			
			if(!user.isLoggedIn()) {
				throw new DaoException("You must log in first");
			}
		} catch (DaoException e) {
			ErrorMessage eMessage =  ErrorMessage.getErrorMessage(e.getMessage(), "user id: " + userId );
			return ResponseEntity.status(500).body(eMessage);
		}
		
		try {
			eventService.addEvent(event);
			Event fromDB = eventService.getEventById(event.getId());
			userEventService.addUserEvent(new UserEvent(userId, event.getId(), UserType.OWNER));
			
			// TODO add notification !!!
			
			return ResponseEntity.status(HttpStatus.CREATED).body(fromDB);
			
		} catch (DaoException e) {
			ErrorMessage eMessage =  ErrorMessage.getErrorMessage(e.getMessage(), getEventIdMessage(event.getId()));
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
	
	/**
	 * DELETE operations
	 * 
	 */
	
	
}
