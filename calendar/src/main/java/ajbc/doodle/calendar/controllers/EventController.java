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

/**
 * Restful api service that receives http requests about the Events in the calendar.
 * @author Anna Aba
 *
 */
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

	/**
	 * Returns all events (not active also) from the database. 
	 * Two optional params - start and end date-time of event. 
	 * If both parameters exist - then the function returns all events in this range.
	 * 
	 * @param start date-time of event.
	 * @param end date-time of event.
	 * @return ResponseEntity with list of events.
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

	/**
	 * Returns all events of an user.
	 * Two optional params - start and end date-time of event. 
	 * If both parameters exist - then the function returns all events of an user that in this range.
	 * 
	 * @param userId the id of the user.
	 * @param start date-time of event.
	 * @param end date-time of event.
	 * @return ResponseEntity with list of events of user.
	 */
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

	/**
	 * Returns all future events of an user.
	 * Two optional params - minutes and hours.
	 * If both parameters exist - then the function returns all future events of an user that coming the next number of minutes and hours.
	 * @param userId the id of the user.
	 * @param minutes the next number of minutes.
	 * @param hours the next number of hours.
	 * @return ResponseEntity with list of future events.
	 */
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

	/**
	 * Adds list of new events of an user to the database. For each event, adds default notification.
	 * @param events list of new events.
	 * @param userId the id of the user.
	 * @return ResponseEntity with the list of new events and their default notifications.
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

	/**
	 * Updates list of existed events of an user in the database.
	 * @param events list of events to update.
	 * @param userId the id of the user.
	 * @return ResponseEntity with the list of updated events.
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

	/**
	 * Adds the event to each guest-user from the guests list. Also for each guest, adds a default notification for this event.
	 * @param eventId the id of the event.
	 * @param userId the id of the user who is the owner of this event.
	 * @param guestIds list of ids of the guests.
	 * @return ResponseEntity with list of the guests with their updated event-list.
	 */
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

	/**
	 * Deletes events by switching their isActive flag to false.
	 * @param eventIds list of ids of the events to delete.
	 * @return ResponseEntity with list of deleted events.
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

	/**
	 * Deletes events completely from the database, and theirs notifications also.
	 * @param eventIds list of ids of the events to delete.
	 * @return ResponseEntity with list of deleted events.
	 */
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
