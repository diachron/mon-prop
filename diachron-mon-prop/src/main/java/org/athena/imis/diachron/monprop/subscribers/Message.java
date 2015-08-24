package org.athena.imis.diachron.monprop.subscribers;

public class Message {
	public static enum MessageStatus {READ, UNREAD}
	private String id;
	private String header;
	private String body;
	private MessageStatus status;
	private String date;
	
	public String getId() {
		return this.id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public MessageStatus getStatus() {
		return status;
	}
	
	public void setStatus(MessageStatus status) {
		this.status = status;
	}

	public void setStatusRead() {
		//TODO persistence storage?
		this.status = MessageStatus.READ;
	}
	
	public void setStatusUnread() {
		//TODO persistence storage?
		this.status = MessageStatus.UNREAD;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
	
}
