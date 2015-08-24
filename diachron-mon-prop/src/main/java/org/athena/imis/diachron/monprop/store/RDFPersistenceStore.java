package org.athena.imis.diachron.monprop.store;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;

import org.athena.imis.diachron.monprop.monitor.Definition;
import org.athena.imis.diachron.monprop.monitor.Topic;
import org.athena.imis.diachron.monprop.subscribers.Message;
import org.athena.imis.diachron.monprop.subscribers.SmtpMessenger;
import org.athena.imis.diachron.monprop.subscribers.Subscriber;
import org.athena.imis.diachron.monprop.subscribers.SubscriberImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtuosoQueryExecution;
import virtuoso.jena.driver.VirtuosoQueryExecutionFactory;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

@Repository
public class RDFPersistenceStore implements PersistenceStore {
	private static final Logger logger = LoggerFactory.getLogger(RDFPersistenceStore.class);
	
	
	public Map<String, Topic> getTopics() {
		logger.info("Retrieve all Topics from RDF Store");
		Hashtable<String, Topic> topics = new Hashtable<String, Topic>();
		Hashtable<String, HashSet<String>> changeTypes =  new Hashtable<String, HashSet<String>>();
		VirtGraph monpropGraph = (VirtGraph) RDFStoreConnection.getGraph(RDFStoreConnection.monpropNamedGraph);
		Query sparql = QueryFactory.create("SELECT ?tid ?l ?did ?period ?ct WHERE { ?tid <"+RDF.type.toString()+"> <"+MonpropOntology.topic.toString()+">; <"+RDFS.label.toString()+"> ?l; <"+MonpropOntology.hasDiachronicDatasetId.toString()+"> ?did; <"+MonpropOntology.hasMonitoringPeriod.toString()+"> ?period. OPTIONAL {?tid <"+MonpropOntology.hasChangeType.toString()+"> ?ct.} }");
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create(sparql, monpropGraph);
		ResultSet results = vqe.execSelect();
		while (results.hasNext()){
			QuerySolution result = results.next();
			if (!topics.containsKey(result.get("tid").toString())){
				topics.put(result.get("tid").toString(), new Topic(new Definition(), result.get("l").toString(), result.get("tid").toString()));
				topics.get(result.get("tid").toString()).getDefinition().setDiachronicDatasetId(result.get("did").toString());
				topics.get(result.get("tid").toString()).getDefinition().setMonitoringPeriod(result.get("period").toString());
				changeTypes.put(result.get("tid").toString(), new HashSet<String>());
				Map<String, Subscriber> subscribers = getSubscribersByTopic(topics.get(result.get("tid").toString()));
				if (!subscribers.isEmpty()) {
					topics.get(result.get("tid").toString()).setSubscribers(subscribers);
				}
			}
			if (result.get("ct")!=null){
				changeTypes.get(result.get("tid").toString()).add(result.get("ct").toString());
			}
		}
		for (String changeTypeKey: changeTypes.keySet()){
			topics.get(changeTypeKey).getDefinition().setChangeTypes(changeTypes.get(changeTypeKey));
		}
		monpropGraph.close();
		return topics;
	}
	
	public void saveTopic(Topic topic) {
		logger.info("Save Topic to RDF Store");
		ArrayList<Statement> statements = new ArrayList<Statement>();
		statements.add(ResourceFactory.createStatement(ResourceFactory.createResource(topic.getId()), RDF.type, MonpropOntology.topic));
		statements.add(ResourceFactory.createStatement(ResourceFactory.createResource(topic.getId()), RDFS.label, ResourceFactory.createPlainLiteral(topic.getLabel())));
		statements.add(ResourceFactory.createStatement(ResourceFactory.createResource(topic.getId()), MonpropOntology.hasDiachronicDatasetId, ResourceFactory.createPlainLiteral(topic.getDefinition().getDiachronicDatasetId())));
		statements.add(ResourceFactory.createStatement(ResourceFactory.createResource(topic.getId()), MonpropOntology.hasMonitoringPeriod, ResourceFactory.createPlainLiteral(topic.getDefinition().getMonitoringPeriod())));
		for (String type: topic.getDefinition().getChangeTypes()){
			statements.add(ResourceFactory.createStatement(ResourceFactory.createResource(topic.getId()), MonpropOntology.hasChangeType, ResourceFactory.createPlainLiteral(type)));
		}
		Model model = RDFStoreConnection.getJenaModel();
		model.add(statements);
		model.close();
	}
	
	public void updateTopic(Topic topic) {
		//TODO update topic persistence store
	}
	
	public void deleteTopic(Topic topic) {
		logger.info("Delete Topic from RDF Store");
		ArrayList<Statement> statements = new ArrayList<Statement>();
		statements.add(ResourceFactory.createStatement(ResourceFactory.createResource(topic.getId()), RDF.type, MonpropOntology.topic));
		statements.add(ResourceFactory.createStatement(ResourceFactory.createResource(topic.getId()), RDFS.label, ResourceFactory.createPlainLiteral(topic.getLabel())));
		statements.add(ResourceFactory.createStatement(ResourceFactory.createResource(topic.getId()), MonpropOntology.hasDiachronicDatasetId, ResourceFactory.createPlainLiteral(topic.getDefinition().getDiachronicDatasetId())));
		statements.add(ResourceFactory.createStatement(ResourceFactory.createResource(topic.getId()), MonpropOntology.hasMonitoringPeriod, ResourceFactory.createPlainLiteral(topic.getDefinition().getMonitoringPeriod())));
		for (String type: topic.getDefinition().getChangeTypes()){
			statements.add(ResourceFactory.createStatement(ResourceFactory.createResource(topic.getId()), MonpropOntology.hasChangeType, ResourceFactory.createPlainLiteral(type)));
		}
		for (Subscriber subscriber: topic.getSubscribers()) {
			statements.add(ResourceFactory.createStatement(ResourceFactory.createResource(topic.getId()), MonpropOntology.hasSubscriber, ResourceFactory.createResource(subscriber.getId())));
		}
		Model model = RDFStoreConnection.getJenaModel();		
		model.remove(statements);
		model.close();
	}
	
	public Map<String, Subscriber> getSubscribersByTopic(Topic topic) {
		logger.info("Retrieve all Subscribers of Topic "+topic.getId()+" from RDF Store");
		Hashtable<String, Subscriber> subscribers = new Hashtable<String, Subscriber>();
		Model model = RDFStoreConnection.getJenaModel();
		ResIterator sids = model.listSubjectsWithProperty(RDF.type, (RDFNode) MonpropOntology.subscriber);
		while (sids.hasNext()){
			Resource sid = sids.next();
			subscribers.put(sid.toString(), new SubscriberImpl());
			subscribers.get(sid.toString()).setId(sid.toString());
			subscribers.get(sid.toString()).setSubscriptionPeriod(sid.getProperty(MonpropOntology.hasSubscriptionPeriod).getObject().toString());
			subscribers.get(sid.toString()).setNotificationType(sid.getProperty(MonpropOntology.hasNotificationType).getObject().toString());
			Hashtable<String, String> subscriptionParameters = new Hashtable<String, String>();
			if (sid.getProperty(MonpropOntology.hasNotificationType).getObject().toString().equals("EMAIL")){
				subscriptionParameters.put(MonpropOntology.hasEmail.toString(), sid.getProperty(MonpropOntology.hasEmail).getObject().toString());
				subscribers.get(sid.toString()).setMessenger(new SmtpMessenger());
			}
			subscribers.get(sid.toString()).setSubscriptionParameters(subscriptionParameters);
			subscribers.get(sid.toString()).setMessages(getMessagesBySubscriber(subscribers.get(sid.toString())));
		}
		model.close();
		return subscribers;
	}
	
	public void saveSubscriber(Subscriber subscriber) {
		logger.info("Save Subscriber to RDF Store");
		ArrayList<Statement> statements = new ArrayList<Statement>();
		statements.add(ResourceFactory.createStatement(ResourceFactory.createResource(subscriber.getId()), RDF.type, MonpropOntology.subscriber));
		statements.add(ResourceFactory.createStatement(ResourceFactory.createResource(subscriber.getId()), MonpropOntology.hasSubscriptionPeriod, ResourceFactory.createPlainLiteral(subscriber.getSubscriptionPeriod())));
		statements.add(ResourceFactory.createStatement(ResourceFactory.createResource(subscriber.getId()), MonpropOntology.hasNotificationType, ResourceFactory.createPlainLiteral(subscriber.getNotificationType())));
		for (String parameterKey: subscriber.getSubscriptionParameters().keySet()){
			statements.add(ResourceFactory.createStatement(ResourceFactory.createResource(subscriber.getId()), ResourceFactory.createProperty(parameterKey), ResourceFactory.createPlainLiteral(subscriber.getSubscriptionParameters().get(parameterKey))));
		}
		Model model = RDFStoreConnection.getJenaModel();		
		model.add(statements);
		model.close();
	}
	
	public void deleteSubscriber(Subscriber subscriber) {
		logger.info("Delete Subscriber from RDF Store");
		for (Message message: subscriber.getMessages()){
			deleteMessage(subscriber.getId(), message);
		}
		ArrayList<Statement> statements = new ArrayList<Statement>();
		statements.add(ResourceFactory.createStatement(ResourceFactory.createResource(subscriber.getId()), RDF.type, MonpropOntology.subscriber));
		statements.add(ResourceFactory.createStatement(ResourceFactory.createResource(subscriber.getId()), MonpropOntology.hasSubscriptionPeriod, ResourceFactory.createPlainLiteral(subscriber.getSubscriptionPeriod())));
		statements.add(ResourceFactory.createStatement(ResourceFactory.createResource(subscriber.getId()), MonpropOntology.hasNotificationType, ResourceFactory.createPlainLiteral(subscriber.getNotificationType())));
		for (String parameterKey: subscriber.getSubscriptionParameters().keySet()){
			statements.add(ResourceFactory.createStatement(ResourceFactory.createResource(subscriber.getId()), ResourceFactory.createProperty(parameterKey), ResourceFactory.createPlainLiteral(subscriber.getSubscriptionParameters().get(parameterKey))));
		}
		Model model = RDFStoreConnection.getJenaModel();		
		model.remove(statements);
		model.close();
	}
	
	public void addSubscriberToTopic(String topicId, String subscriberId) {
		logger.info("Add Subscription (subscriber "+subscriberId+" to topic "+topicId+") to RDF Store");
		Model model = RDFStoreConnection.getJenaModel();
		model.add(ResourceFactory.createResource(topicId), MonpropOntology.hasSubscriber, ResourceFactory.createResource(subscriberId));
		model.close();
	}

	public void removeSubscriberFromTopic(String topicId, String subscriberId) {
		logger.info("Remove Subscription (subscriber "+subscriberId+" from topic "+topicId+") from RDF Store");
		Model model = RDFStoreConnection.getJenaModel();
		model.remove(ResourceFactory.createResource(topicId), MonpropOntology.hasSubscriber, ResourceFactory.createResource(subscriberId));
		model.close();
	}

	public LinkedHashMap<String, Message> getMessagesBySubscriber(Subscriber subscriber) {
		logger.info("Retrieve all Messages of Subscriber "+subscriber.getId()+" from RDF Store");
		LinkedHashMap<String, Message> messages = new LinkedHashMap<String, Message>();
		VirtGraph monpropGraph = (VirtGraph) RDFStoreConnection.getGraph(RDFStoreConnection.monpropNamedGraph);
		Query sparql = QueryFactory.create("SELECT ?mid ?header ?body ?status ?date WHERE { <"+subscriber.getId()+"> <"+MonpropOntology.hasMessage.toString()+"> ?mid. ?mid <"+RDF.type.toString()+"> <"+MonpropOntology.message.toString()+">; <"+MonpropOntology.hasHeader.toString()+"> ?header; <"+MonpropOntology.hasBody.toString()+"> ?body; <"+MonpropOntology.hasStatus.toString()+"> ?status; <"+MonpropOntology.hasDate+"> ?date. }");
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create(sparql, monpropGraph);
		ResultSet results = vqe.execSelect();
		while (results.hasNext()){
			QuerySolution result = results.next();
			messages.put(result.get("mid").toString(), new Message());
			messages.get(result.get("mid").toString()).setId(result.get("mid").toString());
			messages.get(result.get("mid").toString()).setHeader(result.get("header").toString());
			messages.get(result.get("mid").toString()).setBody(result.get("body").toString());
			messages.get(result.get("mid").toString()).setStatus(Message.MessageStatus.valueOf(result.get("status").toString()));
			messages.get(result.get("mid").toString()).setDate(result.get("date").toString());
		}
		monpropGraph.close();
		return messages;
	}
	
	public void saveMessage(String subscriberId, Message message) {
		logger.info("Save Subscriber's Message to RDF Store");
		Model model = RDFStoreConnection.getJenaModel();
		ArrayList<Statement> statements = new ArrayList<Statement>();
		statements.add(ResourceFactory.createStatement(ResourceFactory.createResource(message.getId()), RDF.type, MonpropOntology.message));
		statements.add(ResourceFactory.createStatement(ResourceFactory.createResource(message.getId()), MonpropOntology.hasHeader, ResourceFactory.createPlainLiteral(message.getHeader())));
		statements.add(ResourceFactory.createStatement(ResourceFactory.createResource(message.getId()), MonpropOntology.hasBody, ResourceFactory.createPlainLiteral(message.getBody())));
		statements.add(ResourceFactory.createStatement(ResourceFactory.createResource(message.getId()), MonpropOntology.hasStatus, ResourceFactory.createPlainLiteral(message.getStatus().name())));
		statements.add(ResourceFactory.createStatement(ResourceFactory.createResource(message.getId()), MonpropOntology.hasDate, ResourceFactory.createPlainLiteral(message.getDate())));
		statements.add(ResourceFactory.createStatement(ResourceFactory.createResource(subscriberId), MonpropOntology.hasMessage, ResourceFactory.createPlainLiteral(message.getId())));
		model.add(statements);
		model.close();
	}
	
	public void deleteMessage(String subscriberId, Message message) {
		logger.info("Delete Subscriber's Message from RDF Store");
		Model model = RDFStoreConnection.getJenaModel();
		ArrayList<Statement> statements = new ArrayList<Statement>();
		statements.add(ResourceFactory.createStatement(ResourceFactory.createResource(message.getId()), RDF.type, MonpropOntology.message));
		statements.add(ResourceFactory.createStatement(ResourceFactory.createResource(message.getId()), MonpropOntology.hasHeader, ResourceFactory.createPlainLiteral(message.getHeader())));
		statements.add(ResourceFactory.createStatement(ResourceFactory.createResource(message.getId()), MonpropOntology.hasBody, ResourceFactory.createPlainLiteral(message.getBody())));
		statements.add(ResourceFactory.createStatement(ResourceFactory.createResource(message.getId()), MonpropOntology.hasStatus, ResourceFactory.createPlainLiteral(message.getStatus().name())));
		statements.add(ResourceFactory.createStatement(ResourceFactory.createResource(message.getId()), MonpropOntology.hasDate, ResourceFactory.createPlainLiteral(message.getDate())));
		statements.add(ResourceFactory.createStatement(ResourceFactory.createResource(subscriberId), MonpropOntology.hasMessage, ResourceFactory.createPlainLiteral(message.getId())));
		model.remove(statements);
		model.close();
	}
	
	public static void main(String args[]) {
		RDFStoreConnection.init();
		RDFPersistenceStore store = new RDFPersistenceStore();
		/*Model model = RDFStoreConnection.getJenaModel();
		ArrayList<Statement> statements = new ArrayList<Statement>();
		statements.add(ResourceFactory.createStatement(ResourceFactory.createResource("sub4"), ResourceFactory.createProperty("email"), ResourceFactory.createPlainLiteral("sub4@sub.gr")));
		statements.add(ResourceFactory.createStatement(ResourceFactory.createResource("sub4"), ResourceFactory.createProperty("email1"), ResourceFactory.createPlainLiteral("sub4@sub.com")));
		statements.add(ResourceFactory.createStatement(ResourceFactory.createResource("sub4"), ResourceFactory.createProperty("email2"), ResourceFactory.createPlainLiteral("sub4@sub.gr")));
		statements.add(ResourceFactory.createStatement(ResourceFactory.createResource("sub4"), ResourceFactory.createProperty("email3"), ResourceFactory.createPlainLiteral("sub4@sub.com")));
		model.remove(statements);
		model.close();*/
		/*Definition d = new Definition();
		HashSet<String> ct = new HashSet<String>();
		ct.add("Add_Type_Class");
		d.setDiachronicDatasetId("ID444"); d.setMonitoringPeriod("444"); d.setChangeTypes(ct);
		Topic topic = new Topic(d, "label444", "tid444");
		store.saveTopic(topic);*/
		/*Subscriber sub444 = new SubscriberImpl();
		sub444.setId("sub444"); 
		Hashtable<String, String> hm = new Hashtable<String, String>();
		hm.put(MonpropOntology.hasEmail.toString(), "sub444@sub.com");
		sub444.setSubscriptionParameters(hm);
		sub444.setSubscriptionPeriod("444");
		sub444.setNotificationType("EMAIL");
		store.saveSubscriber(sub444);
		store.addSubscriberToTopic(topic, sub444);*/
		try {
			//store.saveTopic(topic);
			//store.saveSubscriber(sub4);
			//topic.addSubscriber(sub4);
			//Map<String, Topic> lt = store.getTopics();
			//Map<String, Subscriber> ls = store.getSubscribersByTopic("id3");
			//Subscriber sub = ls.get("sub4");
			//lt.get("id3").removeSubscriber(sub);*/
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	/*
	 SPARQL
	SELECT *
	FROM <http://www.diachron-fp7.eu/monprop>
	WHERE { 
	{ ?s ?p ?o }
	}
	*/
	
}
