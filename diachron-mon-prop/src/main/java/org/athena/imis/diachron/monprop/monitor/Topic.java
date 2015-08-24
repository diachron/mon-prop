package org.athena.imis.diachron.monprop.monitor;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;

import org.athena.imis.diachron.monprop.store.PersistenceStore;
import org.athena.imis.diachron.monprop.store.RDFPersistenceStore;
import org.athena.imis.diachron.monprop.subscribers.Subscriber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

public class Topic {
	private Map<String, Subscriber> subscribers;
	private Definition definition;
	private String label;
	private String id;

	public Topic(){
		
	}
	
	public Topic(Definition definition, String label, String id) {
		this.setId(id);
		this.setDefinition(definition);
		this.setLabel(label);
		setSubscribers(new Hashtable<String, Subscriber>());
	}

	private PersistenceStore persistenceStore = new RDFPersistenceStore();
	
	/**
	 * @return the definition
	 */
	public Definition getDefinition() {
		return definition;
	}

	/**
	 * @param definition the definition to set
	 */
	public void setDefinition(Definition definition) {
		this.definition = definition;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	private void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the subscribers
	 */
	public Collection<Subscriber> getSubscribers() {
		return subscribers.values();
	}

	/**
	 * @param subscribers the subscribers to set
	 */
	public void setSubscribers(Map<String,Subscriber> subscribers) {
		this.subscribers = subscribers;
	}
	
	public void addSubscriber(Subscriber subscriber) {
		subscribers.put(subscriber.getId(), subscriber);
		persistenceStore.saveSubscriber(subscriber);
		persistenceStore.addSubscriberToTopic(this.id, subscriber.getId());
	}

	public void removeSubscriber(Subscriber subscriber) {
		subscribers.remove(subscriber.getId());
		persistenceStore.removeSubscriberFromTopic(this.id, subscriber.getId());
		persistenceStore.deleteSubscriber(subscriber);
	}

	public boolean isRelevant(ArchiveEvent ae) {
		if (ae.getDiachronicDatasetId().equals(definition.getDiachronicDatasetId())){
			if (!definition.getChangeTypes().isEmpty()){
				int count = 0;
				for (String changeType: definition.getChangeTypes()){
					if (!ae.getChangeInstances().keySet().contains(changeType)){
						count++;
					}
				}
				if (count==definition.getChangeTypes().size()){
					return false;
				}
				else{
					return true;
				}
			}
			else{
				return true;
			}
		}
		else{
			return false;
		}
	}

	public void publishEvent(ArchiveEvent ae) {
		//TODO take into account subscription period to find valid subscriber
		for (Subscriber subscriber: subscribers.values()) {
			subscriber.publishEvent(ae);
		}
	}

}
