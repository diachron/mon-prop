package org.athena.imis.diachron.monprop.store;

import java.util.Map;

import org.athena.imis.diachron.monprop.monitor.Topic;
import org.athena.imis.diachron.monprop.subscribers.Message;
import org.athena.imis.diachron.monprop.subscribers.Subscriber;

public interface PersistenceStore {
	public Map<String, Topic> getTopics();
	public void saveTopic(Topic topic);
	public void updateTopic(Topic topic);
	public void deleteTopic(Topic topic);
	public Map<String, Subscriber> getSubscribersByTopic(Topic topic);
	public void saveSubscriber(Subscriber subscriber);
	public void deleteSubscriber(Subscriber subscriber);
	public void addSubscriberToTopic(String topicId, String subscriberId);
	public void removeSubscriberFromTopic(String topicId, String subscriberId);
	public Map<String, Message> getMessagesBySubscriber(Subscriber subscriber);
	public void saveMessage(String subscriberId, Message message);
	public void deleteMessage(String subscriberId, Message message);
	
}
