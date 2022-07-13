package ajbc.doodle.calendar.entities;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

class UserTest {

	private User user;
	
	private static final int ID = 100;
	private static final String FIRST_NAME = "Anna", LAST_NAME = "Aba",EMAIL = "annaaba15@gmail.com";
	private static final LocalDate BIRTHDATE = LocalDate.of(1996, 4, 15);
	private static final boolean IS_LOGGED_IN = true, IS_ACTIVE = true;
	private static final Set<Event> EVENTS = new HashSet<Event>();
	private static final Set<Notification> NOTIFICATIONS = new HashSet<Notification>();
	private static final Set<SubscriptionData> SUBSCRIPTIONS = new HashSet<SubscriptionData>();
	
	public UserTest() {
		user = new User();
		user.setId(ID);
		user.setFirstName(FIRST_NAME);
		user.setLastName(LAST_NAME);
		user.setEmail(EMAIL);
		user.setBirthdate(BIRTHDATE);
		user.setLoggedIn(IS_LOGGED_IN);
		user.setActive(IS_ACTIVE);
		
		EVENTS.add(new Event());
		user.setEvents(EVENTS);
		NOTIFICATIONS.add(new Notification());
		user.setNotifications(NOTIFICATIONS);
		SUBSCRIPTIONS.add(new SubscriptionData());
		user.setSubscriptions(SUBSCRIPTIONS);
	}
	
	@Test
	void testFieldsValidation() {
		assertEquals(ID, user.getId());
		assertEquals(FIRST_NAME, user.getFirstName());
		assertEquals(LAST_NAME, user.getLastName());
		assertEquals(EMAIL, user.getEmail());
		assertEquals(BIRTHDATE, user.getBirthdate());
		assertEquals(IS_LOGGED_IN, user.isLoggedIn());
		assertEquals(IS_ACTIVE, user.isActive());
		
		assertArrayEquals(EVENTS.toArray(), user.getEvents().toArray());
		assertArrayEquals(NOTIFICATIONS.toArray(), user.getNotifications().toArray());
		assertArrayEquals(SUBSCRIPTIONS.toArray(), user.getSubscriptions().toArray());
	}
	
	@Test
	void testAddEvent() {
		Event event = new Event();
		
		user.addEvent(event);
		assertTrue(user.getEvents().contains(event));
		
		EVENTS.add(event);
		assertArrayEquals(EVENTS.toArray(), user.getEvents().toArray());
	}
	
	@Test
	void testCopyUser() {
		User newUser = new User();
		
		// will change after copy method
		assertNotEquals(newUser.getFirstName(), user.getFirstName());
		assertNotEquals(newUser.getLastName(), user.getLastName());
		assertNotEquals(newUser.getEmail(), user.getEmail());
		assertNotEquals(newUser.getBirthdate(), user.getBirthdate());
		assertNotEquals(newUser.isLoggedIn(), user.isLoggedIn());
		assertNotEquals(newUser.isActive(), user.isActive());
		
		// will remain the same after copy method
		assertNotEquals(newUser.getId(), user.getId());
		assertNotEquals(newUser.getEvents(), user.getEvents());
		assertNotEquals(newUser.getNotifications(), user.getNotifications());
		assertNotEquals(newUser.getSubscriptions(), user.getSubscriptions());
		
		
		// the method we test
		newUser.copyUser(user);
		
		// must be equal
		assertEquals(newUser.getFirstName(), user.getFirstName());
		assertEquals(newUser.getLastName(), user.getLastName());
		assertEquals(newUser.getEmail(), user.getEmail());
		assertEquals(newUser.getBirthdate(), user.getBirthdate());
		assertEquals(newUser.isLoggedIn(), user.isLoggedIn());
		assertEquals(newUser.isActive(), user.isActive());
		
		// remain without changes
		assertNotEquals(newUser.getId(), user.getId());
		assertNotEquals(newUser.getEvents(), user.getEvents());
		assertNotEquals(newUser.getNotifications(), user.getNotifications());
		assertNotEquals(newUser.getSubscriptions(), user.getSubscriptions());
	}
}
