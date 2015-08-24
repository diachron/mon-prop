package org.athena.imis.diachron.monprop.subscribers;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.athena.imis.diachron.monprop.monitor.ArchiveEvent;
import org.athena.imis.diachron.monprop.store.PersistenceStore;
import org.athena.imis.diachron.monprop.store.RDFPersistenceStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class SubscriberImpl implements Subscriber {
	private Messenger messenger;
	private LinkedHashMap<String , Message> messageMap; 
	private String id;
	private String subscriptionPeriod;
	private String notificationType;
	private Map<String, String> subscriptionParameters;
	
	private PersistenceStore persistenceStore = new RDFPersistenceStore();
	
	public SubscriberImpl(){
		
	}
	
	public void publishEvent(ArchiveEvent ae) {
		Message message = (new MessageFactory()).createMessage(getMessenger().getMessageType(), ae);
		this.messageMap.put(message.getId(), message);
		persistenceStore.saveMessage(this.getId(), message);		
		this.getMessenger().sendMessage(message);
	}
	
	public List<Message> getMessages() {
		//TODO persistence storage
		return new ArrayList<Message>(messageMap.values());
	}
	
	public void setMessages(LinkedHashMap<String, Message> messageMap) {
		this.messageMap = messageMap;
	}
	
	public List<Message> getMessagesByStatus(Message.MessageStatus status) {
		//TODO persistence storage
		return null;
	}
	
	public Message getMessage(String messageId) {
		return messageMap.get(id);
	}
	
	public void deleteMessage(Message message) {
		this.messageMap.remove(message.getId());
		//TODO persistence storage
	}
	
	public void updateMessage(Message message) {
		this.messageMap.put(message.getId(), message);
		//TODO persistence storage
	}
	
	
	public void setMessenger(Messenger messenger) {
		this.messenger = messenger;
	}
	public Messenger getMessenger() {
		return messenger;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String getId() {
		return this.id;
	}

	public String getSubscriptionPeriod() {
		return subscriptionPeriod;
	}

	public void setSubscriptionPeriod(String subscriptionPeriod) {
		this.subscriptionPeriod = subscriptionPeriod;
	}

	public String getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(String notificationType) {
		this.notificationType = notificationType;
	}

	public Map<String, String> getSubscriptionParameters() {
		return subscriptionParameters;
	}

	public void setSubscriptionParameters(Map<String, String> subscriptionParameters) {
		this.subscriptionParameters = subscriptionParameters;
	}
	
}
