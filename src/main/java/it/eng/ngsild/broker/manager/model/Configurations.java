package it.eng.ngsild.broker.manager.model;


//@NoArgsConstructor
//@AllArgsConstructor
//@Getter
//@Setter
//@ToString
public class Configurations {

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
