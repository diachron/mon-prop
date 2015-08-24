package org.athena.imis.diachron.monprop.monitor;

import java.util.Map;

public abstract class Change {
	private String id;
	private String type;
	private Map<String, String> parameters;
	
	public Change(String id, String type, Map<String, String> parameters) {
		this.setId(id);
		this.setType(type);
		this.setParameters(parameters);
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
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return the parameters
	 */
	public Map<String, String> getParameters() {
		return parameters;
	}
	/**
	 * @param parameters the parameters to set
	 */
	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}
	
}
