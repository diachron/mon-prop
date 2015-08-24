package org.athena.imis.diachron.monprop.web.controllers;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.athena.imis.diachron.monprop.monitor.Definition;
import org.athena.imis.diachron.monprop.monitor.Topic;
import org.athena.imis.diachron.monprop.monitor.TopicManager;
import org.athena.imis.diachron.monprop.store.MonpropOntology;
import org.athena.imis.diachron.monprop.subscribers.Messenger;
import org.athena.imis.diachron.monprop.subscribers.SmtpMessenger;
import org.athena.imis.diachron.monprop.subscribers.Subscriber;
import org.athena.imis.diachron.monprop.subscribers.SubscriberFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/topics/")
public class TopicController {

	TopicManager topicManager;
	
    @Autowired(required=true)
    @Qualifier(value="topicManager")
    public void setTopicManager(TopicManager topicManager){
        this.topicManager = topicManager;
    }

	@RequestMapping(value = "/create")
	public @ResponseBody String createTopic(HttpServletRequest request) {
		String diachronicDatasetId = request.getParameter("diachronicDatasetId");
		String monitoringPeriod = request.getParameter("monitoringPeriod"); 
		String label = request.getParameter("label");
		String[] types = request.getParameterValues("changeType");
		Set<String> changeTypes = new HashSet<String>();
		if (types!=null){
			for (int i=0; i<types.length; i++){
				changeTypes.add(types[i]);
			}
		}
		Definition definition = new Definition();
		definition.setDiachronicDatasetId(diachronicDatasetId); definition.setMonitoringPeriod(monitoringPeriod); definition.setChangeTypes(changeTypes);
		Topic topic = new Topic(definition, label, MonpropOntology.monpropOntologyNamespace+UUID.randomUUID().toString());
		topicManager.addTopic(topic);
		return "OK";
	}
	//@RequestParam(value="id", required=true, defaultValue="") String id
	@RequestMapping(value = "/update")
	public @ResponseBody String updateTopic(HttpServletRequest request) {
		String tid = request.getParameter("tid");
		String diachronicDatasetId = request.getParameter("diachronicDatasetId");
		String monitoringPeriod = request.getParameter("monitoringPeriod"); 
		String label = request.getParameter("label");
		String[] types = request.getParameterValues("changeType");
		Set<String> changeTypes = new HashSet<String>();
		if (types!=null){
			for (int i=0; i<types.length; i++){
				changeTypes.add(types[i]);
			}
		}
		Definition definition = new Definition();
		definition.setDiachronicDatasetId(diachronicDatasetId); definition.setMonitoringPeriod(monitoringPeriod); definition.setChangeTypes(changeTypes);
		Topic topic = new Topic(definition, label, tid);
		//creator is able to make an update???
		topicManager.removeTopic(topicManager.getTopic(tid));
		topicManager.addTopic(topic);
		return "OK";
	}

	@RequestMapping(value = "/delete")
	public @ResponseBody String deleteTopic(HttpServletRequest request) {
		String tid = request.getParameter("tid");
		topicManager.removeTopic(topicManager.getTopic(tid));
		return "OK";
	}
	/*
	@RequestMapping(value = "/pause")
	public @ResponseBody String pauseTopic(HttpServletRequest request, @PathVariable("id") String id) {
		topicManager.pauseTopic(topicManager.getTopic(id));
		return "OK";
	}
	*/
	@RequestMapping(value = "/definition")
	public @ResponseBody Definition requestDefinition(HttpServletRequest request) {
		String tid = request.getParameter("tid");
		Definition definition = topicManager.getTopic(tid).getDefinition();
		return definition;
	}
	
	@RequestMapping(value = "/list")
	public @ResponseBody List<Topic> listTopics(HttpServletRequest request) {
		List<Topic> topics = topicManager.getTopics();
		return topics;
	}
	
	@RequestMapping(value = "/subscriber/add")
	public @ResponseBody String addSubscriber(HttpServletRequest request) {
		String tid = request.getParameter("tid");
		Topic topic = topicManager.getTopic(tid);
		String notificationType = request.getParameter("notificationType");
		Messenger messenger = null;
		Map<String, String> subscriptionParameters = new Hashtable<String, String>();
		if (notificationType.equals("EMAIL")){
			messenger = new SmtpMessenger();
			subscriptionParameters.put(MonpropOntology.hasEmail.toString(), request.getParameter("email"));
		}
		String subscriptionPeriod = request.getParameter("subscriptionPeriod");
		Subscriber subscriber = SubscriberFactory.createSubscriber(subscriptionPeriod, notificationType, subscriptionParameters);
		subscriber.setMessenger(messenger);
		topic.addSubscriber(subscriber);
		return "OK";
	}
/*	
	@RequestMapping(value = "/subscriber/add")
	public @ResponseBody String addSubscriber(HttpServletRequest request,
			@PathVariable("id") String id, @PathVariable("sid") String sid) {
		Topic topic = topicManager.getTopic(id);
		//if (topic==null) {System.out.println("topic null");}
		//topic.addSubscriber(subscriber);
		//TODO persistence store
		return "";
	}
*/	
	@RequestMapping(value = "/subscriber/remove")
	public @ResponseBody String removeSubscriber(HttpServletRequest request) {
		String tid = request.getParameter("tid");
		String sid = request.getParameter("sid");
		Topic topic = topicManager.getTopic(tid);
		for (Subscriber subscriber: topic.getSubscribers()){
			if (subscriber.getId().equals(sid)){
				topic.removeSubscriber(subscriber);
			}
		}
		return "OK";
	}

}