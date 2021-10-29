package it.eng.ngsild.broker.manager.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
//@NoArgsConstructor
//@AllArgsConstructor
//@Getter
//@Setter
//@ToString
public class Configurations {
	
	@Id
	private String catalogueId;
	private String contextBrokerUrl;
	
	public Configurations() {
	}
	
	public Configurations (String id, String orionUrl) {
		this.catalogueId=id;
		this.contextBrokerUrl=orionUrl;
	}
	
	public String getCatalogueId() {
		return catalogueId;
	}
	
	public String getContextBrokerUrl() {
		return contextBrokerUrl;
	}

}
