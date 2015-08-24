package org.athena.imis.diachron.monprop.monitor;

import java.util.Map;
import java.util.Set;

public class ArchiveEvent {
	private String diachronicDatasetId;
	private String datasetId;
	private Map<String, Set<Change>> changeInstances;
	
	/**
	 * @return the datasetId
	 */
	public String getDatasetId() {
		return datasetId;
	}
	/**
	 * @param datasetId the datasetId to set
	 */
	public void setDatasetId(String datasetId) {
		this.datasetId = datasetId;
	}
	/**
	 * @return the diachronicDatasetId
	 */
	public String getDiachronicDatasetId() {
		return diachronicDatasetId;
	}
	/**
	 * @param diachronicDatasetId the diachronicDatasetId to set
	 */
	public void setDiachronicDatasetId(String diachronicDatasetId) {
		this.diachronicDatasetId = diachronicDatasetId;
	}
	/**
	 * @return the changeInstances
	 */
	public Map<String, Set<Change>> getChangeInstances() {
		//TODO persistence store reading
		
		return changeInstances;
	}
	
	public void setChangeInstances(Map<String, Set<Change>> changeInstances) {
		this.changeInstances=changeInstances;
	}
	
}
