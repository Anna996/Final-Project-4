//package ajbc.doodle.calendar.controllers;
//
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import ajbc.doodle.calendar.daos.DaoException;
//import ajbc.doodle.calendar.entities.ErrorMessage;
//import ajbc.doodle.calendar.entities.Event;
//import ajbc.doodle.calendar.entities.UserEvent;
//import ajbc.doodle.calendar.entities.UserType;
//import ajbc.doodle.calendar.services.EventService;
//import ajbc.doodle.calendar.services.UserEventService;
//import ajbc.doodle.calendar.services.UserService;
//
//@RestController
//@RequestMapping("/user_event")
//public class UserEventController {
//
//	@Autowired
//	private UserEventService userEventService;
//
//	@GetMapping
//	public ResponseEntity<?> getAllUserEvents() {
//
//		try {
//			List<UserEvent> users = userEventService.getAllUserEvents();
//			return ResponseEntity.ok(users);
//
//		} catch (DaoException e) {
//			ErrorMessage eMessage = ErrorMessage.getErrorMessage(e.getMessage(), "try again later...");
//			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(eMessage);
//		}
//	}
//
//	@GetMapping("/{userId}/{eventId}")
//	public ResponseEntity<?> getUserEventByIDs(@PathVariable int userId, @PathVariable int eventId) {
//
//		try {
//			UserEvent userEvent = userEventService.getUserEventByIDs(userId, eventId);
//			return ResponseEntity.ok(userEvent);
//
//		} catch (DaoException e) {
//			ErrorMessage eMessage = ErrorMessage.getErrorMessage(e.getMessage(),
//					"userId: %s , eventId: %s".formatted(userId, eventId));
//			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(eMessage);
//		}
//	}
//}
