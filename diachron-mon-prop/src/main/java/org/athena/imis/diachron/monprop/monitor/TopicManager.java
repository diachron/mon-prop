package org.athena.imis.diachron.monprop.monitor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.athena.imis.diachron.monprop.store.PersistenceStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class TopicManager implements Runnable {

	//private static TopicManager instance = null;
	private Map<String , Topic> topics = new LinkedHashMap<String, Topic>();
	private static boolean storeInit = false;
	private PersistenceStore persistenceStore;
	
    @Autowired(required=true)
    @Qualifier(value="persistenceStore")
    public void setPersistenceStore(PersistenceStore persistenceStore){
        this.persistenceStore = persistenceStore;
        this.init();
    }
    
	public TopicManager() {
		// Exists only to defeat instantiation.
	}
/*
	public static TopicManager getInstance() {
		if (instance == null) {
			instance = new TopicManager();
			instance.init();
		}
		return instance;
	}
	
*/	
	private void init() {
		if (!storeInit) {
			topics =  persistenceStore.getTopics();
			storeInit = true;
		}
	}

	
	@Override
	public void run() {
		//System.out.println("started");
		while (true) {
			/*while (PublishQueue.getInstance().hasJob()) {
				ArchiveEvent archiveEvent = PublishQueue.getInstance().pollJob();
				handleArchiveEvent(archiveEvent);
				
			} */
			try {
				///System.out.println("sleeping");
				Thread.sleep(1000*60);
			} catch (InterruptedException e) {
				//e.printStackTrace();
			}
		}
		
	}
	
	public void handleArchiveEvent(ArchiveEvent archiveEvent) {
		for (Topic topic : topics.values()) {
			if (topic.isRelevant(archiveEvent)) {
				topic.publishEvent(archiveEvent);
			}
		}
	}
	
	public void addTopic(Topic topic) {
		topics.put(topic.getId(), topic);
		persistenceStore.saveTopic(topic);
	}
	
	public void removeTopic(Topic topic) {
		// do we care if is it active??
		topics.remove(topic);
		persistenceStore.deleteTopic(topic);
	}
	/*
	public void pauseTopic(Topic topic) {
		//TODO pause topic
	}
	*/
	public Topic getTopic(String id) {
		return topics.get(id);
	}

	public List<Topic> getTopics() {
		return new ArrayList<Topic>(topics.values());
	}
	
	
}
