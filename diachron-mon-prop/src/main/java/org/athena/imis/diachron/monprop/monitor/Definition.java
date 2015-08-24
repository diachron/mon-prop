package org.athena.imis.diachron.monprop.monitor;

import java.util.Set;

public class Definition {
	private String diachronicDatasetId;
	private String monitoringPeriod;
	private Set<String> changeTypes;
	
	public String getDiachronicDatasetId() {
		return diachronicDatasetId;
	}

	public void setDiachronicDatasetId(String diachronicDatasetId) {
		this.diachronicDatasetId = diachronicDatasetId;
	}
	
	public String getMonitoringPeriod() {
		return monitoringPeriod;
	}
	
	public void setMonitoringPeriod(String monitoringPeriod) {
		this.monitoringPeriod = monitoringPeriod;
	}
	
	public Set<String> getChangeTypes() {
		return changeTypes;
	}
	
	public void setChangeTypes(Set<String> changeTypes) {
		this.changeTypes = changeTypes;
	}
	
}
