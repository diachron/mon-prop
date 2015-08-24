package org.athena.imis.diachron.monprop.subscribers;

import java.util.List;

public interface Messenger {
	public void sendMessage(Message message);
	public Object publishMessages(List<Message> messages);
	public String getMessageType();

}
