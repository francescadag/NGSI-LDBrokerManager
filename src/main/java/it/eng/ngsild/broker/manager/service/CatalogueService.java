package it.eng.ngsild.broker.manager.service;

import java.net.MalformedURLException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import javax.ws.rs.core.MediaType;
import org.apache.http.HttpResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import it.eng.idra.beans.dcat.DcatDataset;
import it.eng.idra.beans.dcat.DcatDistribution;
import it.eng.idra.beans.dcat.DcatProperty;
import it.eng.idra.beans.dcat.FoafAgent;
import it.eng.idra.beans.dcat.SkosConceptTheme;
import it.eng.idra.beans.dcat.SkosPrefLabel;
import it.eng.idra.beans.dcat.VcardOrganization;
import it.eng.idra.beans.odms.OdmsCatalogue;
import it.eng.idra.utils.restclient.RestClient;
import it.eng.idra.utils.restclient.RestClientImpl;
import it.eng.ngsild.broker.manager.model.Configurations;


@Service
public class CatalogueService  {
	
    /** The logger. */
	private static Logger logger = LogManager.getLogger(CatalogueService.class);

	public CatalogueService() {
	}

	
	@Value("${idra.basepath}")
	private String idraBasePath;
	
	public int start(Configurations config) throws Exception {
		return addCatalogueInCb(config);
	}
	
	public int delete(Configurations config) throws Exception {
		return deleteCatalogueFromCb(config);
	}

	
	public int addCatalogueInCb(Configurations config) throws Exception {
		
		String nodeId = config.getCatalogueId();
		String urlCB = config.getContextBrokerUrl() + "/ngsi-ld/v1/";
		
	      ArrayList<String> allDatasets = new ArrayList<String>();
	      ArrayList<String> allEntities = new ArrayList<String>();
	      
	      // I get the catalogue through Idra API
	      RestTemplate restTemplate = new RestTemplate();
	      
	      OdmsCatalogue node = restTemplate
	    	      .getForObject(idraBasePath + "/Idra/api/v1/" + "/client/catalogues/" + nodeId, OdmsCatalogue.class);
	      logger.info("CATALOGUE NAME: " + node.getName());

	      // I get the datasets through Idra API
	      ResponseEntity<String> resultDatasets = restTemplate.getForEntity(idraBasePath + "/Idra/api/v1/" 
	      + "/client/catalogues/" + nodeId + "/datasets", String.class);
	      ObjectMapper objectMapper = new ObjectMapper();
	      JsonNode jsonNode = objectMapper.readTree(resultDatasets.getBody());
	      
	      DcatDataset[] datasetsList = objectMapper.readValue(jsonNode.get("results"), DcatDataset[].class);
	      List<DcatDataset> datasets = Arrays.asList(datasetsList);

	      // ADDING DATASETS
	      for (DcatDataset dataset : datasets) {

	        String idDataset = "\"urn:ngsi-ld:Catalogue:dataset:" + dataset.getId() + "\"";
	        if (!allDatasets.contains(idDataset)) {
	          allDatasets.add(idDataset);
	        }
	        
	        // AGENT of the DATASET
	        if (dataset.getCreator() != null) {
	          FoafAgent creator = dataset.getCreator();
//	          String identificator = creator.getId();
	          String identificator = creator.getIdentifier().getValue();
	          identificator = identificator.replaceAll("[^a-zA-Z0-9]", "");

	          String idDs = "urn:ngsi-ld:id:" + identificator;     
	          
	          String agentType = "";
	          if (creator.getType() != null) {
	        	  agentType = creator.getType().getValue();
	          }
	         
	          String api = urlCB + "entities/" + idDs;
	          int status = restRequest(api, "", "GET");
	          if (status != 200) {
	          
	            // ADDING AGENT CREATOR
	            String type = "AgentDCAT-AP";
	            String agent = creator.getName().getValue();
	      
	            String data = "{ \"id\": \"" + idDs + "\", \"type\": \"" + type + "\","
		            + "\"name\": { " 
		            + "\"type\": \"Property\","
		            + "\"value\": \"" + agent + "\" }," 
	                + "\"agentType\": { " 
	                + "\"type\": \"Property\","
	                + "\"value\": \"" + agentType + "\" }" 
	                + " }";
	            
	            if (!allEntities.contains(data)) {
	              allEntities.add(data);
	            }

	         }
	        }

	        if (dataset.getPublisher() != null) {
	          FoafAgent publisher = dataset.getPublisher();
//	          String identificator = publisher.getId();
	          String identificator = publisher.getIdentifier().getValue();
	          identificator = identificator.replaceAll("[^a-zA-Z0-9]", "");

	          String idDs = "urn:ngsi-ld:id:" + identificator;     
	          
	          String agentType = "";
	          if (publisher.getType() != null) {
	        	  agentType = publisher.getType().getValue();
	          }

	          String api = urlCB + "entities/" + idDs;
	          int status = restRequest(api, "", "GET");
	          if (status != 200) {
	          
	            // ADDING AGENT PUBLISHER
	            String type = "AgentDCAT-AP";
	            String agent = publisher.getName().getValue();
	      
	            String data = "{ \"id\": \"" + idDs + "\", \"type\": \"" + type + "\","
	                + "\"name\": { " 
	                + "\"type\": \"Property\","
	                + "\"value\": \"" + agent + "\" }," 
	                + "\"agentType\": { " 
	                + "\"type\": \"Property\","
	                + "\"value\": \"" + agentType + "\" }" 
	                + " }";
	            
	            if (!allEntities.contains(data)) {
	              allEntities.add(data);
	            }
	            
	          }       
	        }

	        if (dataset.getRightsHolder() != null) {
	          FoafAgent holder = dataset.getRightsHolder();
	          
//	          String identificator = holder.getId();
	          String identificator = holder.getIdentifier().getValue();
	          identificator = identificator.replaceAll("[^a-zA-Z0-9]", "");
	          String idDs = "urn:ngsi-ld:id:" + identificator; 
	          
	          String agentType = "";
	          if (holder.getType() != null) {
	        	  agentType = holder.getType().getValue();
	          }

	          String api = urlCB + "entities/" + idDs;
	          int status = restRequest(api, "", "GET");
	          if (status != 200) {
	          
	            // ADDING AGENT RIGHTS HOLDER
	            String type = "AgentDCAT-AP";
	            String agent = holder.getName().getValue();
	      
	            String data = "{ \"id\": \"" + idDs + "\", \"type\": \"" + type + "\","
	                + "\"name\": { " 
	                + "\"type\": \"Property\","
	                + "\"value\": \"" + agent + "\" }," 
	                + "\"agentType\": { " 
	                + "\"type\": \"Property\","
	                + "\"value\": \"" + agentType + "\" }" 
	                + " }";
	            
	            if (!allEntities.contains(data)) {
	              allEntities.add(data);
	            }
			    
	          }
	        }
	        
	                
	        // ADDING DISTRIBUTIONS
	        ArrayList<String> allDistributions = new ArrayList<String>();
	        
	        List<DcatDistribution> distributions = dataset.getDistributions();
	        for (DcatDistribution d : distributions) {

	          String idDistribution = "\"urn:ngsi-ld:Dataset:items:" + d.getId() + "\"";
	          if (!allDistributions.contains(idDistribution)) {
	            allDistributions.add(idDistribution);
	          }

	          String identificat = d.getId();
	          
	          // I get the Distributions through Idra API
	          String apiD = idraBasePath + "/Idra/api/v1/" + "/client/catalogues/" + nodeId + "/datasets/" 
	        		  + dataset.getId() + "/distributions/" + d.getId();
	          Map<String, String> headers = new HashMap<String, String>();
	          headers.put("Content-Type", "application/json");
	          RestClient client = new RestClientImpl();
	          String returnedJson = "";
	          HttpResponse response = client.sendGetRequest(apiD, headers);
	          returnedJson = client.getHttpResponseBody(response);
	          JSONObject distrib = new JSONObject(returnedJson);
	          
	          String format = distrib.optString("format", "");
	          String mediaType = distrib.optString("mediaType", "");
	          String titleD = distrib.optString("title", "");
	          String des = distrib.optString("description", "");
	          String stat = distrib.optString("status", ""); // a value between Completed, Deprecated, Under Development, Withdrawn 
	          
	          String idDis = "urn:ngsi-ld:DistributionDCAT-AP:id:" + identificat;
	                
	          //String api = urlCB + "entities/" + idDis;
	          //int status = restRequest(api, "", "GET");
	          //if (status != 200) {
	            
	            String typeDis = "DistributionDCAT-AP";
	            
	            String titleDis = titleD.replaceAll("[^a-zA-Z0-9]", " ");

	            String descr = des.replaceAll("[^a-zA-Z0-9]", " ");

	            ArrayList<String> languageList = new ArrayList<String>();
	            for(DcatProperty lang: d.getLanguage()) {
	            	if (lang.getValue() != "")
	            		languageList.add("\"" + lang.getValue() + "\"");
	            }
	            
	            String byteSize = "";
	            if (d.getByteSize() != null) {
	            	byteSize = d.getByteSize().getValue();
	            }
	            String checksum = "";
	            if (d.getChecksum() != null) {
	            	//checksum = d.getChecksum().getChecksumValue().getValue().replaceAll("\"", "");
	            	checksum = d.getChecksum().getChecksumValue().getValue();
	            }

	            // aggiungere dateCreated e dateModified come TIMESTAMP della Entity?
	            
	            String dataDis = "{ \"id\": \"" + idDis + "\", \"type\": \"" + typeDis + "\","
	                + "\"description\": { " 
	                + "\"type\": \"Property\","
	                + "\"value\": \"" + descr + "\" }," 
	                + "\"title\": { "
	                + "\"type\": \"Property\","
	                + "\"value\": [ \"" + titleDis + "\" ]" 
	                + " },"
	                + "\"releaseDate\": { "
	                + "\"type\": \"Property\","
	                + "\"value\": { "
	                + "\"@type\": \"DateTime\","
	                + "\"@value\": \"" + d.getReleaseDate().getValue() + "\"  }"
	                + " }, "
	                + "\"modifiedDate\": { "
	                + "\"type\": \"Property\","
	                + "\"value\": { "
	                + "\"@type\": \"DateTime\","
	                + "\"@value\": \"" + d.getUpdateDate().getValue() + "\"  }"
	                + " }, "
	                + "\"accessUrl\": { "
	                + "\"type\": \"Property\","
	                + "\"value\": [ \"" + d.getAccessURL().getValue() +  "\" ]" 
	                + " },"
	                + "\"downloadURL\": { "
	                + "\"type\": \"Property\","
	                + "\"value\": [ \"" + d.getDownloadURL().getValue() + "\" ]" 
	                + " },"
	                + "\"license\": { "
	                + "\"type\": \"Property\","
	                + "\"value\": \"" + d.getLicense().getName().getValue() + "\""
	                + " },"
	                + "\"format\": { "
	                + "\"type\": \"Property\","
	                + "\"value\": \"" + format + "\""
	                + " },"
	                + "\"mediaType\": { "
	                + "\"type\": \"Property\","
	                + "\"value\": \"" + mediaType + "\""
	                + " },"
	                + "\"rights\": { "
	                + "\"type\": \"Property\","
	                + "\"value\": \"" + d.getRights().getValue() + "\""
	                + " },"
	                + "\"language\": { "
	                + "\"type\": \"Property\","
	                + "\"value\": " +  languageList.toString()    
	                + " },"
	                + "\"byteSize\": { "
	                + "\"type\": \"Property\","
	                + "\"value\": \"" + byteSize + "\""
	                + " },"
	                + "\"checksum\": { "
	                + "\"type\": \"Property\","
	                + "\"value\": \"" + checksum + "\""
	                + " },"
	                + "\"status\": { "
	                + "\"type\": \"Property\","
	                + "\"value\": \"" + stat + "\""
	                + " }"
	                + " }";
	            	
	            if (!allEntities.contains(dataDis)) {
	              allEntities.add(dataDis);
	            }
	          //}
	        } 

	        String idDs = "urn:ngsi-ld:Dataset:id:" + dataset.getId();     
	        //String api = urlCB + "entities/" + idDs;
	        //int status = restRequest(api, "", "GET");
	        //if (status != 200) {
	        
	          // ADDING DATASET
	          String des = dataset.getDescription().getValue();
	          String descr = des.replaceAll("[^a-zA-Z0-9]", " ");
	          
	          String t = dataset.getTitle().getValue();
	          //String title = t.replaceAll("[^a-zA-Z0-9]", " ");
	          String title = t.replaceAll("\"", "");
	          title = title.replaceAll("\'", " ");

	          String creator = "";
	          if (dataset.getCreator() != null) {
	            creator = dataset.getCreator().getName().getValue();
	          }
	          String publisher = "";
	          if (dataset.getPublisher() != null) {
	            publisher = dataset.getPublisher().getName().getValue();
	          }
	          
	          ArrayList<String> contacts = new ArrayList<String>();
	          for (int i = 0; i < dataset.getContactPoint().size(); i++) {
	        	VcardOrganization v = dataset.getContactPoint().get(i);
	            contacts.add("\"" + v.getHasEmail().getValue() + "\""); 
	          }
//	          if (contacts.size() == 0)
//	        	  contacts.add("");
	          
	          ArrayList<String> themes = new ArrayList<String>();
	          List<SkosConceptTheme> theme = dataset.getTheme();
	          for(SkosConceptTheme tema: theme) {
	            List<SkosPrefLabel> lab = tema.getPrefLabel();
	            for (SkosPrefLabel label: lab)
	            	if(label.getValue() != "") {
	            		themes.add("\"" + label.getValue() + "\"");
	            	}
	          }
	          if (themes.size() == 0)
	        	  themes.add("");
	          
	          ArrayList<String> keywords = new ArrayList<String>();
	          for (String keyw: dataset.getKeywords()) {
	          	if(keyw != "")
	          		keywords.add("\"" + keyw + "\"");
	          }
	          if (keywords.size() == 0)
	        	  keywords.add("");
	          
	          ArrayList<String> documentation = new ArrayList<String>();
	          for (DcatProperty doc: dataset.getDocumentation()) {
	          	if(doc.getValue() != "")
	          		documentation.add("\"" + doc.getValue() + "\"");
	          }

	          ArrayList<String> language = new ArrayList<String>();
	          for (DcatProperty lan: dataset.getLanguage()) {
	          	if(lan.getValue() != "")
	          		language.add("\"" + lan.getValue() + "\"");
	          }
	          ArrayList<String> otherIdentifier = new ArrayList<String>();
	          for (DcatProperty otId: dataset.getOtherIdentifier()) {
	          	if(otId.getValue() != "") {
	          		otherIdentifier.add("\"" + otId.getValue() + "\"");
	          	}
	          }
	          ArrayList<String> provenance = new ArrayList<String>();
	          for (DcatProperty pr: dataset.getProvenance()) {
	          	if(pr.getValue() != "")
	          		provenance.add("\"" + pr.getValue() + "\"");
	          }
	          String frequency = "";
	          if (dataset.getFrequency() != null) {
	        	  frequency = dataset.getFrequency().getValue();
	          }
	          
//	          if (dataset.getIsVersionOf() != null) {
//	        	  System.out.println("isVersOf: " + dataset.getIsVersionOf());
//	          }
//	          if (dataset.getHasVersion() != null) {
//	        	  System.out.println("hasVers: " + dataset.getHasVersion());
//	          }
//	          ArrayList<String> hasVersion = new ArrayList<String>();
//	          for (DcatProperty hasV: dataset.getHasVersion()) {
//	          	if(hasV.getValue() != "")
//	          		hasVersion.add("\"" + hasV.getValue() + "\"");
//	          }
	          String startDate = "";
	          String endDate = "";
	          if (dataset.getTemporalCoverage() != null) {
	        	  startDate = dataset.getTemporalCoverage().getStartDate().getValue();
	        	  endDate = dataset.getTemporalCoverage().getEndDate().getValue();
	          }
	          String version = "";
	          if (dataset.getVersion() != null) {
	        	  version = dataset.getVersion().getValue();
	          }
	          ArrayList<String> versionNotes = new ArrayList<String>();
	          for (DcatProperty verNotes: dataset.getVersionNotes()) {
	          	if(verNotes.getValue() != "")
	          		versionNotes.add("\"" + verNotes.getValue() + "\"");
	          }

	          // dateCreated è la creazione della Entity
	          // dateModified Timestamp of the last modification of the entity. 
	          // This will usually be allocated by the storage platform
	   
	          String typeDs = "Dataset";
	          String dataDs = "{ \"id\": \"" + idDs + "\", \"type\": \"" + typeDs + "\","
	              + "\"description\": { " 							
	              + "\"type\": \"Property\","
	              + "\"value\": \"" + descr + "\" },"
	              + "\"alternateName\": { " 
	              + "\"type\": \"Property\","
	              + "\"value\": \"\" },"
	              + "\"title\": { "
	              + "\"type\": \"Property\","
	              + "\"value\": [ \"" + title + "\" ]" 
	              + " },"
	              + "\"landingPage\": { "
	              + "\"type\": \"Property\","
	              + "\"value\": [ \"" + dataset.getLandingPage().getValue() + "\" ]" 
	              + " },"
	              + "\"datasetDistribution\": { "
	              + "\"type\": \"Property\","
	              + "\"value\": " + allDistributions.toString() 
	              + " },"
	              + "\"contactPoint\": { "
	              + "\"type\": \"Property\","
	              + "\"value\": " + contacts.toString() 		
	              + " },"
	              + "\"keyword\": { "
	              + "\"type\": \"Property\","
	              + "\"value\": " + keywords.toString() 		
	              + " },"
	              + "\"theme\": { "
	              + "\"type\": \"Property\","
	              + "\"value\": " + themes.toString() 			
	              + " },"
	              + "\"documentation\": { "
	              + "\"type\": \"Property\","
	              + "\"value\": " + documentation.toString()    
	              + " },"
//	              + "\"hasVersion\": { "
//	              + "\"type\": \"Property\","
//	              + "\"value\": " + hasVersion.toString()    	
//	              + " },"
	              + "\"language\": { "
	              + "\"type\": \"Property\","
	              + "\"value\": " + language.toString()    
	              + " },"
	              + "\"otherIdentifier\": { "
	              + "\"type\": \"Property\","
	              + "\"value\": " + otherIdentifier.toString()    
	              + " },"
	              + "\"provenance\": { "
	              + "\"type\": \"Property\","
	              + "\"value\": " + provenance.toString()    
	              + " },"
	              + "\"versionNotes\": { "
	              + "\"type\": \"Property\","
	              + "\"value\": " + versionNotes.toString()    
	              + " },"
	              + "\"version\": { " 							
	              + "\"type\": \"Property\","
	              + "\"value\": \"" + version + "\" },"
	              + "\"temporal\": { "
	              + "\"type\": \"Property\","
	              + "\"value\": [ { "
	              + "\"@type\": \"DateTime\","
	              + "\"@value\": \"" + startDate + "\"  },"
	              + "{ "
	    	      + "\"@type\": \"DateTime\","
	    	      + "\"@value\": \"" + endDate + "\" "
	              + " } ] }, "
	              + "\"accessRights\": { " 
	              + "\"type\": \"Property\","
	              + "\"value\": \"" + dataset.getAccessRights().getValue() + "\" },"
	              + "\"dateCreated\": { "
	              + "\"type\": \"Property\","
	              + "\"value\": { "
	              + "\"@type\": \"DateTime\","
	              + "\"@value\": \"" + dataset.getReleaseDate().getValue() + "\"  }"
	              + " }, "
	              + "\"dateModified\": { "
	              + "\"type\": \"Property\","
	              + "\"value\": { "
	              + "\"@type\": \"DateTime\","
	              + "\"@value\": \"" + dataset.getUpdateDate().getValue() + "\"  }"
	              + " }, "
	              + "\"publisher\": { " 
	              + "\"type\": \"Property\","
	              + "\"value\": \"" + publisher + "\" },"
	              + "\"creator\": { " 
	              + "\"type\": \"Property\","
	              + "\"value\": \"" + creator + "\" },"
	              + "\"frequency\": { " 
	              + "\"type\": \"Property\","
	              + "\"value\": \"" + frequency + "\" },"		
	              + "\"source\": { " 
	              + "\"type\": \"Property\","
	              + "\"value\": \"" + dataset.getLandingPage().getValue() + "\" }"		// ricontrolla
	              + " }";
	          
	          if (!allEntities.contains(dataDs)) {
	            allEntities.add(dataDs);
	          }
	          
	      }

	      int identificator = node.getId();
	      String id = "urn:ngsi-ld:Catalogue:id:" + identificator;
	      String api = urlCB + "entities/" + id;
          //String api = urlCB + "entities/";
	      //int status = restRequest(api, "", "GET");
	      //if (status != 200) {

	        ZonedDateTime dateModified = node.getLastUpdateDate();
	        DateTime modif = new DateTime(dateModified.toInstant().toEpochMilli(),
		            DateTimeZone.forTimeZone(TimeZone.getTimeZone(dateModified.getZone())));
	        
//	        System.out.println("REG DATE: " + node.getRegisterDate());
//	        String regDate = "";
//	        if (node.getRegisterDate() != null) {
//		        ZonedDateTime dateCreate = node.getRegisterDate();
//		        DateTime created = new DateTime(dateCreate.toInstant().toEpochMilli(),
//		            DateTimeZone.forTimeZone(TimeZone.getTimeZone(dateCreate.getZone())));
//		        regDate = created.toString();
//	        }

//	        System.out.println(" ----------- location :" + node.getLocation());
//	        System.out.println(" ----------- location descrp:" + node.getLocationDescription());
	        String des = node.getDescription();
	        String description = des.replaceAll("[^a-zA-Z0-9]", " ");
	        
	        String n = node.getName();
	        String name = n.replaceAll("[^a-zA-Z0-9]", " ");
	      
	        String type = "CatalogueDCAT-AP";
	        String data = "{ \"id\": \"" + id + "\", \"type\": \"" + type + "\","
	            + "\"description\": { " 
	            + "\"type\": \"Property\","
	            + "\"value\": \"" + description + "\" }," 
	            + "\"publisher\": { " 
	            + "\"type\": \"Property\","
	            + "\"value\": \"" + node.getPublisherName() + "\" }," 
	            + "\"title\": { "
	            + "\"type\": \"Property\","
	            + "\"value\": [ \"" + name + "\" ]" 
	            + " }, "
	            + "\"name\": { " 
	            + "\"type\": \"Property\","
	            + "\"value\": \"Catalogue\" },"
	            + "\"alternateName\": { " 
	            + "\"type\": \"Property\","
	            + "\"value\": \"\" },"
	            + "\"homepage\": { " 
	            + "\"type\": \"Property\","
	            + "\"value\": \"" + node.getHomepage() + "\" },"
	            + "\"dataProvider\": { " 
	            + "\"type\": \"Property\","
	            + "\"value\": \"\" },"
	            + "\"source\": { " 
	            + "\"type\": \"Property\","
	            + "\"value\": \"" + node.getHost() + "\" },"        // rincontrolla
//				+ "\"dateCreated\": { "
//				+ "\"type\": \"Property\","
//				+ "\"value\": { "
//				+ "\"@type\": \"DateTime\","
//				+ "\"@value\": \"" + regDate + "\"  }"
//				+ " }, "
	            + "\"dateModified\": { "
	            + "\"type\": \"Property\","
	            + "\"value\": { "
	            + "\"@type\": \"DateTime\","
	            + "\"@value\": \"" + modif + "\"  }"
	            + " }, "
	            + "\"dataset\": { "
	            + "\"type\": \"Relationship\","
	            + "\"object\": " + allDatasets.toString()             
	            + " }, "
	            + "\"language\": { "
	            + "\"type\": \"Property\","
	            + "\"value\": [ \"" + node.getCountry() + "\" ]" 
	            + " } "
	            + " }";
	        
	        if (!allEntities.contains(data)) {
	          allEntities.add(data);
	        }

	      
	      identificator = node.getId();
	      id = "urn:ngsi-ld:id:" + identificator;   
	      api = urlCB + "entities/" + id;
	      //int status = restRequest(api, "", "GET");
	      //if (status != 200) {
	        type = "AgentDCAT-AP";
	        String agent = node.getPublisherName();
	        identificator = node.getId();
	        id = "urn:ngsi-ld:id:" + identificator;
	        data = "{ \"id\": \"" + id + "\", \"type\": \"" + type + "\","
	            + "\"name\": { " 
	            + "\"type\": \"Property\","
	            + "\"value\": \"" + agent + "\" }" 
	            + " }";
	        
	        if (!allEntities.contains(data)) {
	          allEntities.add(data);
	        }
	        
	        
	      // POST CREATE request in BATCH
	      int status = 200;
	      api = urlCB + "entityOperations/create";
	      if (allEntities.size() > 200) {
	    	
	        status = postRequestWithCheck(allEntities, api, 200);
	        logger.info("STATUS CREATE CATALOGUE IN THE CB: " + status);
	      } else {
	    	// POST CREATE BATCH 
	        status = restRequest(api, allEntities.toString(), "POST");
	        logger.info("STATUS CREATE CATALOGUE IN THE CB: " + status);
	      }
//	  if (status != 200 && status != 207 && status != 204 
//	        && status != 201 && status != 301) {
//	      throw new Exception("------------ STATUS POST - CONTEXT BROKER: " + status);
//	  }
	  return status;
	}
	

	  /**
	   * Delete a Catalogue from the CB.
	  // * @throws Exception 
	  // * @throws MalformedURLException 
	   */
	  public int deleteCatalogueFromCb(Configurations config) throws MalformedURLException, Exception {
		  
		  String nodeId = config.getCatalogueId();
		  String urlCB = config.getContextBrokerUrl() + "/ngsi-ld/v1/";
	      
	      // I get the Catalogue through Idra API
	      RestTemplate restTemplate = new RestTemplate();
	      ResponseEntity<String> result = restTemplate.getForEntity(idraBasePath + "/Idra/api/v1/" 
	      + "/client/catalogues/" + nodeId, String.class); 
	      logger.info("RESULT GET CATALOGUE: " + result.getStatusCodeValue());
	      
	      OdmsCatalogue node = restTemplate
	    	      .getForObject(idraBasePath + "/Idra/api/v1/" + "/client/catalogues/" + nodeId, OdmsCatalogue.class);
	      logger.info("CATALOGUE NAME to delete: " + node.getName());

	      // I get the Datasets through Idra API
	      ResponseEntity<String> resultDatasets = restTemplate.getForEntity(idraBasePath + "/Idra/api/v1/" 
	      + "/client/catalogues/" + nodeId + "/datasets", String.class);
	      ObjectMapper objectMapper = new ObjectMapper();
	      JsonNode jsonNode = objectMapper.readTree(resultDatasets.getBody());

	      DcatDataset[] datasetsList = objectMapper.readValue(jsonNode.get("results"), DcatDataset[].class);
	      List<DcatDataset> datasets = Arrays.asList(datasetsList);

	      // DELETING
	      ArrayList<String> listId = new ArrayList<String>();
	  
	      // CATALOGUE DELETING
	      int identificator = node.getId();
	      String id = "\"urn:ngsi-ld:Catalogue:id:" + identificator + "\"";
	      listId.add(id);
	      
	      // CATALOGUE AGENT DELETING
	      identificator = node.getId();
	      id = "\"urn:ngsi-ld:id:" + identificator + "\"";
	      listId.add(id);
	      
	      // DATASETS DELETING 
	      //List<DcatDataset> datasets = MetadataCacheManager.getAllDatasetsByOdmsCatalogue(node.getId());
	      for (DcatDataset dataset : datasets) {   
	        String identif = dataset.getId();
	        String idDs = "\"urn:ngsi-ld:Dataset:id:" + identif + "\"";
	        if (!listId.contains(idDs)) {
	          listId.add(idDs);
	        }
	        
	        // DATASETS AGENTS DELETING 
	        // 1. CREATORS
	        if (dataset.getCreator() != null) {
	          identif = dataset.getCreator().getIdentifier().getValue();
	          identif = identif.replaceAll("[^a-zA-Z0-9]", "");
	          idDs = "\"urn:ngsi-ld:id:" + identif + "\"";
	          if (!listId.contains(idDs)) {
	            listId.add(idDs);
	            
	          }
	        }
	        // 2. PUBLISHERS
	        if (dataset.getPublisher() != null) {    
	          identif = dataset.getPublisher().getIdentifier().getValue();
	          identif = identif.replaceAll("[^a-zA-Z0-9]", "");
	          idDs = "\"urn:ngsi-ld:id:" + identif + "\"";
	          if (!listId.contains(idDs)) {
	            listId.add(idDs);
	            
	          }
	        }
	        // 3. RIGHT HOLDERS
	        if (dataset.getRightsHolder() != null) {  
	          identif = dataset.getRightsHolder().getIdentifier().getValue();
	          identif = identif.replaceAll("[^a-zA-Z0-9]", "");
	          idDs = "\"urn:ngsi-ld:id:" + identif + "\"";
	          if (!listId.contains(idDs)) {
	            listId.add(idDs);
	            
	          }
	        }
	        
	        // DISTRIBUTIONS DELETING
	        List<DcatDistribution> distributions = dataset.getDistributions();
	        for (DcatDistribution d : distributions) {
	          String identificat = d.getId();
	          String idDis = "\"urn:ngsi-ld:DistributionDCAT-AP:id:" + identificat + "\"";

	          if (!listId.contains(idDis)) {
	            listId.add(idDis);
	            
	          }

	        } 
	      }

	      String data =  listId.toString();
	      String api = urlCB + "entityOperations/delete";
	      int status = 200;
	      if (listId.size() > 200) {
	    	
	        status = postRequestWithCheck(listId, api, 200);
	        
	      } else {
	    	  
	    	// POST DELETE BATCH 
	        status = restRequest(api, data, "POST");
	        logger.info("STATUS DELETE " + status);
//	        if (status != 200 && status != 207 && status != 204 && status != -1 
//	            && status != 201 && status != 301) {
//	          throw new Exception("------------ STATUS DELETE DISTRIBUTION - CONTEXT BROKER: " + status);
//	        }
	      }
	      return status;
	  }
	  

	  /**
	   * Post Request on CB with Check on the number of Entities.
	// * @throws Exception exception.
	   * 
	   */
	  public int postRequestWithCheck(List<String> listId, String api, int maxNumberOfEntities) throws Exception {
	    String data =  listId.toString();
	    
	    int r = (listId.size()) % maxNumberOfEntities;
	    int tot = listId.size() - r;
	    int numberOfPost = tot / maxNumberOfEntities;
	    
	    int i = 0;
	    int l = maxNumberOfEntities - 1; // 199
	    
	    int status = 200;
	    
	    for (int k = 0; k < numberOfPost; k++) {
	      List<String> list = listId.subList(i, l + 1);
	      data =  list.toString();

	      status = restRequest(api, data, "POST");
	      
	      if (status != 200 && status != 207 && status != 204 && status != -1 
	          && status != 201 && status != 301) {
	    	  return status;
//	        throw new Exception("------------ STATUS POST - CONTEXT BROKER: " + status);
	      }
	      i += maxNumberOfEntities;
	      l += maxNumberOfEntities;
	    }
	  
	    List<String> list = listId.subList(i, (listId.size()));
	    if (list.size() != 0) {
		    data =  list.toString(); 
	
		    status = restRequest(api, data, "POST");
		    if (status != 200 && status != 207 && status != 204 && status != -1 
		        && status != 201 && status != 301) {
		    	return status;
//		      throw new Exception("------------ STATUS POST - CONTEXT BROKER: " + status);
		    }
	    }
	    return status;
	  }	
	  
	
	  
		private int restRequest(String api, String data, String requestType) throws Exception {
		    Map<String, String> headers = new HashMap<String, String>();
		    headers.put("Content-Type", "application/json");
		    RestClient client = new RestClientImpl();
		    HttpResponse response = null;
		    if (requestType.equals("POST")) {
		      response = client.sendPostRequest(api, data,
		          MediaType.APPLICATION_JSON_TYPE, headers); 
		    }
		    else if (requestType.equals("PUT")) {
		      response = client.sendPutRequest(api, data,
		          MediaType.APPLICATION_JSON_TYPE, headers); 
		    }
		    else if (requestType.equals("GET")) {
		      response = client.sendGetRequest(api, headers);
		    }
		    else if (requestType.equals("DELETE")) {
			      response = client.sendDeleteRequest(api, headers); 
			}
		    int status = client.getStatus(response);
		    return status;
	}
	
	
}
