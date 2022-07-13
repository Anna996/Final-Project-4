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

@RestController
public class PushController {

	@Autowired
	private UserService userService;

	private final ServerKeys serverKeys;

	private final Map<String, Subscription> subscriptions = new ConcurrentHashMap<>();

	public PushController(ServerKeys serverKeys, CryptoService cryptoService, ObjectMapper objectMapper) {
		this.serverKeys = serverKeys;
	}

	@GetMapping(path = "/publicSigningKey", produces = "application/octet-stream")
	public byte[] publicSigningKey() {
		return this.serverKeys.getPublicKeyUncompressed();
	}

	@GetMapping(path = "/publicSigningKeyBase64")
	public String publicSigningKeyBase64() {
		return this.serverKeys.getPublicKeyBase64();
	}

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

	@PostMapping("/isSubscribed")
	public boolean isSubscribed(@RequestBody SubscriptionEndpoint subscription) {
		return this.subscriptions.containsKey(subscription.getEndpoint());
	}
}