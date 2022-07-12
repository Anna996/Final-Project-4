const subscribeButton = document.getElementById('subscribeButton');
const unsubscribeButton = document.getElementById('unsubscribeButton');

const notificationOutput = document.getElementById('notification');

const email = document.getElementById('email');
const alert = document.getElementById('alert');
const alert2 = document.getElementById('alert2');

if ("serviceWorker" in navigator) {
	try {
		checkSubscription();
		init();
	} catch (e) {
		console.error('error init(): ' + e);
	}

	subscribeButton.addEventListener('click', () => {
		subscribe().catch(e => {
			if (Notification.permission === 'denied') {
				console.warn('Permission for notifications was denied');
			} else {
				console.error('error subscribe(): ' + e);
			}
		});
	});

	unsubscribeButton.addEventListener('click', () => {
		unsubscribe().catch(e => console.error('error unsubscribe(): ' + e));
	});
}


async function checkSubscription() {
	const registration = await navigator.serviceWorker.ready;
	const subscription = await registration.pushManager.getSubscription();
	if (subscription) {

		const response = await fetch("/isSubscribed", {
			method: 'POST',
			body: JSON.stringify({ endpoint: subscription.endpoint }),
			headers: {
				"content-type": "application/json"
			}
		});
		const subscribed = await response.json();

		if (subscribed) {
			subscribeButton.disabled = true;
			unsubscribeButton.disabled = false;
			email.disabled = true;
			alert.style.display = "none";
			alert2.style.display = "none";
		}

		return subscribed;
	}

	return false;
}

async function init() {
	fetch('/publicSigningKey')
		.then(response => response.arrayBuffer())
		.then(key => this.publicSigningKey = key)
		.finally(() => console.info('Application Server Public Key fetched from the server'));

	await navigator.serviceWorker.register("/sw.js", {
		scope: "/"
	});

	await navigator.serviceWorker.ready;
	console.info('Service Worker has been installed and is ready');
	navigator.serviceWorker.addEventListener('message', event => displayLastMessages());

	//displayLastMessages();
}

function displayLastMessages() {
	caches.open('data').then(dataCache => {
		dataCache.match('notification')
			.then(response => response ? response.text() : '')
			.then(txt => {

				// id - 0 ; date - 1 ; time - 2 ; title - 3 ; message - 4
				txt = txt.split(";");

				space = "&emsp;";
				id = "<h2>notification #" + txt[0] + "</h2>";
				title = "<h3> Title:" + space + txt[3] + "</h3>";
				message = "<h3> Message:" + space + txt[4] + "</h3>";
				date = "<h3> Date:" + space + txt[1] + "</h3>";
				time = "<h3> Time:" + space + txt[2] + "</h3>";

				notificationOutput.innerHTML = id + title + message + date + time;

			});
	});
}

async function unsubscribe() {
	const registration = await navigator.serviceWorker.ready;
	const subscription = await registration.pushManager.getSubscription();
	if (subscription) {
		const successful = await subscription.unsubscribe();
		if (successful) {
			console.info('Unsubscription successful');

			await fetch("/unsubscribe/" + email.value, {
				method: 'POST',
				body: JSON.stringify({ endpoint: subscription.endpoint }),
				headers: {
					"content-type": "application/json"
				}
			});

			console.info('Unsubscription info sent to the server');

			subscribeButton.disabled = false;
			unsubscribeButton.disabled = true;
			email.disabled = false;
			notificationOutput.innerHTML = "";
		}
		else {
			console.error('Unsubscription failed');
		}
	}
}

async function subscribe() {
	if (email.value === "") {
		alert.style.display = "block";
		alert2.style.display = "none";
		return;
	} else {
		alert.style.display = "none";
		alert2.style.display = "none";
	}
	const registration = await navigator.serviceWorker.ready;
	const subscription = await registration.pushManager.subscribe({
		userVisibleOnly: true,
		applicationServerKey: this.publicSigningKey
	});

	console.info(`Subscribed to Push Service: ${subscription.endpoint}`);

	const response = await fetch("/subscribe/" + email.value, {
		method: 'POST',
		body: JSON.stringify(subscription),
		headers: {
			"content-type": "application/json"
		}
	});

	console.info('Subscription info sent to the server');

	const subscribed = await response.json();
	
	
	if (subscribed) {
		subscribeButton.disabled = true;
		unsubscribeButton.disabled = false;
		email.disabled = true;
		console.info("Subscription successed");
	} else {
		console.info("Subscription Failed");
		alert2.style.display = "block";
	}
}
