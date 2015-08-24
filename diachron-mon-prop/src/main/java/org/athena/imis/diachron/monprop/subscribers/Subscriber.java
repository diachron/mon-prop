package org.athena.imis.diachron.monprop.subscribers;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.athena.imis.diachron.monprop.monitor.ArchiveEvent;

public interface Subscriber {
	public String getId();
	public void setId(String id);
	
	public void publishEvent(ArchiveEvent ae);
	
	public List<Message> getMessages();
	public void setMessages(LinkedHashMap<String, Message> messageMap);
	public List<Message> getMessagesByStatus(Message.MessageStatus status);
	public Message getMessage(String messageId);
	public void deleteMessage(Message message);
	public void updateMessage(Message message);
	
	public void setMessenger(Messenger messenger);
	public Messenger getMessenger();
	
	public String getSubscriptionPeriod();
	public void setSubscriptionPeriod(String subscriptionPeriod);
	public String getNotificationType();
	public void setNotificationType(String notificationType);
	public Map<String, String> getSubscriptionParameters();
	public void setSubscriptionParameters(Map<String, String> subscriptionParameters);

}
