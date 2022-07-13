package ajbc.doodle.calendar.services;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse.BodyHandlers;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ajbc.doodle.calendar.Application;
import ajbc.doodle.calendar.ServerKeys;
import ajbc.doodle.calendar.entities.webpush.PushMessage;
import ajbc.doodle.calendar.entities.webpush.Subscription;

@Service
public class NotificationSenderService {

	private final ServerKeys serverKeys;

	private final CryptoService cryptoService;

	private final HttpClient httpClient;

	private final Algorithm jwtAlgorithm;

	private final ObjectMapper objectMapper;

	public NotificationSenderService(ServerKeys serverKeys, CryptoService cryptoService, ObjectMapper objectMapper) {
		this.serverKeys = serverKeys;
		this.cryptoService = cryptoService;
		this.httpClient = HttpClient.newHttpClient();
		this.objectMapper = objectMapper;
		this.jwtAlgorithm = Algorithm.ECDSA256(this.serverKeys.getPublicKey(), this.serverKeys.getPrivateKey());
	}

	public void sendNotificationToUser(Map<String, Subscription> subs, String notificationInfo) {
		try {
			sendPushMessageToAllSubscribers(subs, new PushMessage("You have new notification", notificationInfo));
		} catch (JsonProcessingException e) {
			System.err.println(e.getMessage());
		}
	}

	private void sendPushMessageToAllSubscribers(Map<String, Subscription> subs, Object message)
			throws JsonProcessingException {

		Set<String> failedSubscriptions = new HashSet<>();

		for (Subscription subscription : subs.values()) {
			try {
				byte[] result = this.cryptoService.encrypt(this.objectMapper.writeValueAsString(message),
						subscription.getKeys().getP256dh(), subscription.getKeys().getAuth(), 0);
				boolean remove = sendPushMessage(subscription, result);
				if (remove) {
					failedSubscriptions.add(subscription.getEndpoint());
				}
			} catch (InvalidKeyException | NoSuchAlgorithmException | InvalidAlgorithmParameterException
					| IllegalStateException | InvalidKeySpecException | NoSuchPaddingException
					| IllegalBlockSizeException | BadPaddingException e) {
				Application.logger.error("send encrypted message", e);
			}
		}

		failedSubscriptions.forEach(subs::remove);
	}

	/**
	 * @return true if the subscription is no longer valid and can be removed, false
	 *         if everything is okay
	 */
	private boolean sendPushMessage(Subscription subscription, byte[] body) {
		String origin = null;
		try {
			URL url = new URL(subscription.getEndpoint());
			origin = url.getProtocol() + "://" + url.getHost();
		} catch (MalformedURLException e) {
			Application.logger.error("create origin", e);
			return true;
		}

		Date today = new Date();
		Date expires = new Date(today.getTime() + 12 * 60 * 60 * 1000);

		String token = JWT.create().withAudience(origin).withExpiresAt(expires)
				.withSubject("mailto:example@example.com").sign(this.jwtAlgorithm);

		URI endpointURI = URI.create(subscription.getEndpoint());

		Builder httpRequestBuilder = HttpRequest.newBuilder();
		if (body != null) {
			httpRequestBuilder.POST(BodyPublishers.ofByteArray(body)).header("Content-Type", "application/octet-stream")
					.header("Content-Encoding", "aes128gcm");
		} else {
			httpRequestBuilder.POST(BodyPublishers.ofString(""));
			// httpRequestBuilder.header("Content-Length", "0");
		}

		HttpRequest request = httpRequestBuilder.uri(endpointURI).header("TTL", "180")
				.header("Authorization", "vapid t=" + token + ", k=" + this.serverKeys.getPublicKeyBase64()).build();
		try {
			HttpResponse<Void> response = this.httpClient.send(request, BodyHandlers.discarding());

			switch (response.statusCode()) {
			case 201:
				Application.logger.info("Push message successfully sent: {}", subscription.getEndpoint());
				break;
			case 404:
			case 410:
				Application.logger.warn("Subscription not found or gone: {}", subscription.getEndpoint());
				// remove subscription from our collection of subscriptions
				return true;
			case 429:
				Application.logger.error("Too many requests: {}", request);
				break;
			case 400:
				Application.logger.error("Invalid request: {}", request);
				break;
			case 413:
				Application.logger.error("Payload size too large: {}", request);
				break;
			default:
				Application.logger.error("Unhandled status code: {} / {}", response.statusCode(), request);
			}
		} catch (IOException | InterruptedException e) {
			Application.logger.error("send push message", e);
		}

		return false;
	}
}
