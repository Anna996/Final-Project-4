package ajbc.doodle.calendar.controllers;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import ajbc.doodle.calendar.ServerKeys;
import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.entities.webpush.Subscription;
import ajbc.doodle.calendar.entities.webpush.SubscriptionEndpoint;
import ajbc.doodle.calendar.services.CryptoService;
import ajbc.doodle.calendar.services.UserService;

/**
 * Restful api service that enables users to subscribe to the calendar push notification
 * service, and receive their notifications to their browser.
 * 
 * @author Anna Aba
 *
 */
@RestController
public class PushController {

	@Autowired
	private UserService userService;

	private final ServerKeys serverKeys;

	private final Map<String, Subscription> subscriptions = new ConcurrentHashMap<>();

	/**
	 * Constructor - gets pair of keys.
	 * @param serverKeys - Voluntary Application Server Identification keys.
	 */
	public PushController(ServerKeys serverKeys) {
		this.serverKeys = serverKeys;
	}

	/**
	 * Get request that returns the public key of the server.
	 * @return array of bytes which represents the public key.
	 */
	@GetMapping(path = "/publicSigningKey", produces = "application/octet-stream")
	public byte[] publicSigningKey() {
		return this.serverKeys.getPublicKeyUncompressed();
	}

	/**
	 *  Get request that returns the Base64 public key of the server.
	 * @return the public key as a Base64-encoded string.
	 */
	@GetMapping(path = "/publicSigningKeyBase64")
	public String publicSigningKeyBase64() {
		return this.serverKeys.getPublicKeyBase64();
	}

	/**
	 * Subscribe user and his browser to the push notification service.
	 * @param subscription object that holds the user's subscription data.
	 * @param email the email of the user.
	 * @return true if logged in successfully.
	 */
	@PostMapping("/subscribe/{email}")
	public boolean subscribe(@RequestBody Subscription subscription, @PathVariable(required = false) String email) {
		try {
			userService.loginUser(email, subscription.getEndpoint(), subscription.getKeys().getP256dh(),
					subscription.getKeys().getAuth());
			this.subscriptions.put(subscription.getEndpoint(), subscription);
			System.out.println("Subscription added with email " + email);
			return true;
		} catch (DaoException e) {
			System.out.println("User login failed: " + e.getMessage());
			return false;
		}
	}

	/**
	 * Unsubscribe user and his browser from the push notification service.
	 * @param subscription object that holds the user's subscription data.
	 * @param email the email of the user.
	 */
	@PostMapping("/unsubscribe/{email}")
	public void unsubscribe(@RequestBody SubscriptionEndpoint subscription,
			@PathVariable(required = false) String email) {
		try {
			userService.logoutUser(email, subscription.getEndpoint());
			this.subscriptions.remove(subscription.getEndpoint());
			System.out.println("Subscription with email " + email + " got removed!");
		} catch (DaoException e) {
			System.out.println("User Logout failed: " + e.getMessage());
		}
	}

	/**
	 * Returns true if browser is subscribed to the push notification service.
	 * @param subscription object that holds the user's subscription data.
	 * @return true if the browser is already subscribed.
	 */
	@PostMapping("/isSubscribed")
	public boolean isSubscribed(@RequestBody SubscriptionEndpoint subscription) {
		return this.subscriptions.containsKey(subscription.getEndpoint());
	}
}