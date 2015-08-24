package org.athena.imis.diachron.monprop.subscribers;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.athena.imis.diachron.monprop.store.MonpropOntology;

public class SubscriberFactory {
	public static Subscriber createSubscriber(String subscriptionPeriod, String notificationType, Map<String, String> subscriptionParameters) {
		Subscriber subscriber = new SubscriberImpl();
		UUID uuid = UUID.randomUUID();
		LinkedHashMap<String, Message> messageMap = new LinkedHashMap<String, Message>();
        subscriber.setId(MonpropOntology.monpropOntologyNamespace + uuid.toString());
        subscriber.setMessages(messageMap);
        subscriber.setSubscriptionPeriod(subscriptionPeriod);
        subscriber.setNotificationType(notificationType);
        subscriber.setSubscriptionParameters(subscriptionParameters);
        return subscriber;
	}

}
