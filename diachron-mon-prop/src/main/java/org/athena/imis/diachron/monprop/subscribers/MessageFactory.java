package org.athena.imis.diachron.monprop.subscribers;

import java.util.Date;
import java.util.UUID;

import org.athena.imis.diachron.monprop.monitor.ArchiveEvent;
import org.athena.imis.diachron.monprop.store.MonpropOntology;

public class MessageFactory {
	
	public Message createMessage(String messageType, ArchiveEvent ae) {
		Message message = new Message();
		UUID uuid = UUID.randomUUID();
		message.setId(MonpropOntology.monpropOntologyNamespace + uuid.toString());
		message.setHeader(ae.getDiachronicDatasetId());
		String date = new Date().toString();
		String body = "Date: "+date+"\n"+"New dataset version: "+ae.getDatasetId()+"\n";
		//TODO construct better body
		message.setBody(body);
		message.setStatusUnread();
		message.setDate(date);
		return message;
		
	}

}
