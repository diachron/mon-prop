package org.athena.imis.diachron.monprop.store;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

/**
 * 
 * This class contains jena definitions for the ontology/vocabulary terms used in monitoring and propagation service.
 *
 */
public class MonpropOntology {

	public static final String diachronNamespace = "http://www.diachron-fp7.eu/";
	public static final String diachronResourcePrefix = diachronNamespace + "resource/";
	public static final String changesOntologyNamespace = "http://www.diachron-fp7.eu/changes/"; 
	public static final String monpropOntologyNamespace = "http://www.diachron-fp7.eu/monprop/";
	public static final String sparqlResultsNamespace = "http://www.w3.org/2005/sparql-results#";
	public static final String provNamespace = "http://www.w3.org/ns/prov#";
	
	public static final Resource topic;
	public static final Resource subscriber;
	public static final Resource message;
		
	public static final Property hasDiachronicDatasetId;
	public static final Property hasMonitoringPeriod;
	public static final Property hasChangeType;
	public static final Property hasSubscriber;
	public static final Property hasSubscriptionPeriod;
	public static final Property hasNotificationType;
	public static final Property hasEmail;
	public static final Property hasMessage;
	public static final Property hasHeader;
	public static final Property hasBody;
	public static final Property hasStatus;
	public static final Property hasDate;
		
	static {
		topic = ResourceFactory.createResource(monpropOntologyNamespace + "Topic");		
		subscriber = ResourceFactory.createResource(monpropOntologyNamespace + "Subscriber");
		message = ResourceFactory.createResource(monpropOntologyNamespace + "Message");
		
		hasDiachronicDatasetId = ResourceFactory.createProperty(monpropOntologyNamespace + "hasDiachronicDatasetId");
		hasMonitoringPeriod = ResourceFactory.createProperty(monpropOntologyNamespace + "hasMonitoringPeriod");
		hasChangeType = ResourceFactory.createProperty(monpropOntologyNamespace + "hasChangeType");
		hasSubscriber = ResourceFactory.createProperty(monpropOntologyNamespace + "hasSubscriber");
		hasSubscriptionPeriod = ResourceFactory.createProperty(monpropOntologyNamespace + "hasSubscriptionPeriod");
		hasNotificationType = ResourceFactory.createProperty(monpropOntologyNamespace + "hasNotificationType");
		hasEmail = ResourceFactory.createProperty(monpropOntologyNamespace + "hasEmail");
		hasMessage = ResourceFactory.createProperty(monpropOntologyNamespace + "hasMessage");
		hasHeader = ResourceFactory.createProperty(monpropOntologyNamespace + "hasHeader");
		hasBody = ResourceFactory.createProperty(monpropOntologyNamespace + "hasBody");
		hasStatus = ResourceFactory.createProperty(monpropOntologyNamespace + "hasSatus");
		hasDate = ResourceFactory.createProperty(monpropOntologyNamespace + "hasDate");
					
	}
	
}
