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
import ajbc.doodle.calendar.entities.Event;
import ajbc.doodle.calendar.entities.User;
import ajbc.doodle.calendar.services.EventService;
import ajbc.doodle.calendar.services.UserService;

@RestController
@RequestMapping("/events")
public class EventController {

	@Autowired
	private EventService eventService;

	@Autowired
	private UserService userService;

	/**
	 * GET operations
	 * 
	 */

	@GetMapping
	public ResponseEntity<?> getAllEvents(@RequestParam(required = false) String start,
			@RequestParam(required = false) String end) {
		List<Event> events;

		try {
			if (start != null && end != null) {
				events = eventService.getEventsInRange(start, end);
			} else {
				events = eventService.getAllEvents();
			}

			return ResponseEntity.ok(events);

		} catch (DaoException e) {
			ErrorMessage eMessage = ErrorMessage.getErrorMessage(e.getMessage(), "fetching data failed");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(eMessage);
		}
	}

	@GetMapping("/user/{id}")
	public ResponseEntity<?> getEventsByUserId(@PathVariable("id") int userId,
			@RequestParam(required = false) String start, @RequestParam(required = false) String end) {
		List<Event> events;

		try {

			if (start != null && end != null) {
				events = eventService.getEventsInRangeByUserId(userId, start, end);
			} else {
				events = eventService.getEventsByUserId(userId);
			}

			return ResponseEntity.ok(userService.filterByUserNotifications(events, userId));

		} catch (DaoException e) {
			ErrorMessage eMessage = ErrorMessage.getErrorMessage(e.getMessage(), "fetching data failed");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(eMessage);
		}
	}

	@GetMapping("/user/{id}/future")
	public ResponseEntity<?> getFutureEventsByUserId(@PathVariable("id") int userId,
			@RequestParam(required = false) Integer minutes, @RequestParam(required = false) Integer hours) {
		List<Event> events;

		try {
			if (minutes != null && hours != null) {
				events = eventService.getFutureEventsByUserIdMinutesAndHours(userId, minutes, hours);
			} else {
				events = eventService.getFutureEventsByUserId(userId);
			}

			return ResponseEntity.ok(userService.filterByUserNotifications(events, userId));

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
	public ResponseEntity<?> addEvents(@RequestBody List<Event> events, @RequestParam(required = true) int userId) {

		if (events == null || events.size() == 0) {
			ErrorMessage eMessage = ErrorMessage.getErrorMessage("didn't get events info", "failed to create events");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(eMessage);
		}

		try {
			eventService.addEvents(events, userId);
			// fetch from DB in order to show his default notifications
			events = eventService.getEventsByIds(events.stream().map(event -> event.getId()).collect(Collectors.toList()));
			return ResponseEntity.status(HttpStatus.CREATED).body(events);

		} catch (DaoException e) {
			ErrorMessage eMessage = ErrorMessage.getErrorMessage(e.getMessage(), "failed to create these events");
			return ResponseEntity.status(500).body(eMessage);
		}
	}

	/**
	 * PUT operations
	 * 
	 */

	@PutMapping
	public ResponseEntity<?> updateEvents(@RequestBody List<Event> events, @RequestParam(required = true) int userId) {

		if (events == null || events.size() == 0) {
			ErrorMessage eMessage = ErrorMessage.getErrorMessage("didn't get events info", "failed to update events");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(eMessage);
		}

		try {
			eventService.updateEvents(events, userId);
			return ResponseEntity.ok(events);

		} catch (DaoException e) {
			ErrorMessage eMessage = ErrorMessage.getErrorMessage(e.getMessage(), "failed to update these events");
			return ResponseEntity.status(500).body(eMessage);
		}
	}

	@PutMapping("/{id}/guests")
	public ResponseEntity<?> addGuestsToEvent(@PathVariable("id") int eventId,
			@RequestParam(required = true) int userId, @RequestBody List<Integer> guestIds) {

		try {
			eventService.addGuestsToEvent(eventId, userId, guestIds);
			List<User> guests = userService.getUsersByIds(guestIds);
			return ResponseEntity.ok(userService.filterByUserNotifications(guests));

		} catch (DaoException e) {
			ErrorMessage eMessage = ErrorMessage.getErrorMessage(e.getMessage(),
					"failed to send this event to these guests");
			return ResponseEntity.status(500).body(eMessage);
		}
	}

	/**
	 * DELETE operations
	 * 
	 */

	@DeleteMapping
	public ResponseEntity<?> softDeleteEvents(@RequestBody List<Integer> eventIds) {

		if (eventIds == null || eventIds.size() == 0) {
			ErrorMessage eMessage = ErrorMessage.getErrorMessage("didn't get events info", "failed to delete events");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(eMessage);
		}

		try {
			eventService.softDeleteEvents(eventIds);
			List<Event> events = eventService.getEventsByIds(eventIds);
			return ResponseEntity.ok(events);

		} catch (DaoException e) {
			ErrorMessage eMessage = ErrorMessage.getErrorMessage(e.getMessage(), "failed to delete these events");
			return ResponseEntity.status(500).body(eMessage);
		}
	}

	@DeleteMapping("/delete")
	public ResponseEntity<?> hardDeleteEvents(@RequestBody List<Integer> eventIds) {

		if (eventIds == null || eventIds.size() == 0) {
			ErrorMessage eMessage = ErrorMessage.getErrorMessage("didn't get events info", "failed to delete events");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(eMessage);
		}

		try {
			List<Event> events = eventService.getEventsByIds(eventIds);
			eventService.hardDeleteEvents(eventIds);
			return ResponseEntity.ok(events);

		} catch (DaoException e) {
			ErrorMessage eMessage = ErrorMessage.getErrorMessage(e.getMessage(), "failed to delete these events");
			return ResponseEntity.status(500).body(eMessage);
		}
	}
}
