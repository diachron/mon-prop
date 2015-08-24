package org.athena.imis.diachron.monprop.monitor;

import java.util.concurrent.LinkedBlockingQueue;

public class PublishQueue<T> {

	private static PublishQueue<?> instance = null;
	private static Thread thread = null;
	
	private LinkedBlockingQueue<T> queue = new LinkedBlockingQueue<T>();

	private PublishQueue() {
		// Exists only to defeat instantiation.
	}
/*
	@SuppressWarnings("unchecked")
	public static PublishQueue<ArchiveEvent> getInstance() {
		if (instance == null) {
			thread = new Thread(TopicManager.getInstance());
			thread.start();
			instance = new PublishQueue<ArchiveEvent>();
		}
		return (PublishQueue<ArchiveEvent>)instance;
		
	}*/
	
	public void addJob(T job) {
		queue.add(job);
		thread.interrupt();
	}
	
	public T pollJob() {
		return queue.poll();
	}
	
	public boolean hasJob() {
		return !queue.isEmpty();
	}
}
