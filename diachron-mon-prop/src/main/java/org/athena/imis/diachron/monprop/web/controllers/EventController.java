package org.athena.imis.diachron.monprop.web.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.athena.imis.diachron.monprop.monitor.ArchiveEvent;
import org.athena.imis.diachron.monprop.monitor.Change;
import org.athena.imis.diachron.monprop.monitor.TopicManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/events/")
public class EventController {

	TopicManager topicManager;

	@Autowired(required = true)
	@Qualifier(value = "topicManager")
	public void setTopicManager(TopicManager topicManager) {
		this.topicManager = topicManager;
	}

	@RequestMapping(value = "/publish")
	public @ResponseBody String publish(HttpServletRequest request) {
		String diachronicDatasetId = request.getParameter("diachronicDatasetId");
		String datasetId = request.getParameter("datasetId");
		Map<String, Set<Change>> ci = new HashMap<String, Set<Change>>();
		ArchiveEvent ae = new ArchiveEvent();
		ae.setDiachronicDatasetId(diachronicDatasetId);
		ae.setDatasetId(datasetId);
		ae.setChangeInstances(ci);
		topicManager.handleArchiveEvent(ae);
		return "OK";
	}

}