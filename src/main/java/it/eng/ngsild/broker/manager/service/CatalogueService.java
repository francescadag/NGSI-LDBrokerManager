package it.eng.ngsild.broker.manager.service;

import java.net.MalformedURLException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import javax.ws.rs.core.MediaType;
import org.apache.http.HttpResponse;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.json.JSONArray;
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

	public CatalogueService() {
	}

	
	@Value("${idra.basepath}")
	private String idraBasePath;
	
	// Riceve un nodeId
	public int start(Configurations config) throws Exception {
		return addCatalogueInOrion(config);
	}
	
	// Riceve un nodeId
	public int delete(Configurations config) throws Exception {
		return deleteCatalogueFromOrion(config);
	}
	
	
	
	public int addCatalogueInOrion(Configurations config) throws Exception {
		
		String nodeId = config.getCatalogueId();
		String urlCB = config.getContextBrokerUrl() + "/ngsi-ld/v1/";
		
	      ArrayList<String> allDatasets = new ArrayList<String>();
	      ArrayList<String> allEntities = new ArrayList<String>();
	      
	      // OTTENGO IL CATALOGO
	      RestTemplate restTemplate = new RestTemplate();
	      
	      OdmsCatalogue node = restTemplate
	    	      .getForObject(idraBasePath + "/Idra/api/v1/" + "/client/catalogues/" + nodeId, OdmsCatalogue.class);
	      System.out.println("\n NOME CATALOGUE: " + node.getName());

	      // OTTENGO I DATASETS
	      ResponseEntity<String> resultDatasets = restTemplate.getForEntity(idraBasePath + "/Idra/api/v1/" 
	      + "/client/catalogues/" + nodeId + "/datasets", String.class);
	      ObjectMapper objectMapper = new ObjectMapper();
	      JsonNode jsonNode = objectMapper.readTree(resultDatasets.getBody());

	      //String count = objectMapper.readValue(jsonNode.get("count").toString(), String.class);
	      DcatDataset[] datasetsList = objectMapper.readValue(jsonNode.get("results"), DcatDataset[].class);
	      List<DcatDataset> datasets = Arrays.asList(datasetsList);
	      //System.out.println("\n DATASETS: " + resultDatasets.getBody());
	      //String s = Integer.toString(datasetsList.size());
	      
	      
	      // AGGIUNGO I DATASETS
	      //List<DcatDataset> datasets = MetadataCacheManager.getAllDatasetsByOdmsCatalogue(node.getId());
	      for (DcatDataset dataset : datasets) {

	        String idDataset = "\"urn:ngsi-ld:Catalogue:dataset:" + dataset.getId() + "\"";
	        if (!allDatasets.contains(idDataset)) {
	          allDatasets.add(idDataset);
	        }
	        
	        // AGENT del DATASET
	        // CONTROLLO: durante la sync, se un AGENT CREATOR è già presente NON aggiungerlo
	        if (dataset.getCreator() != null) {
//		        if (dataset.getCreator() != null && (!dataset.getCreator().getIdentifier()
//		        		.getValue().equals("n.d") || !dataset.getCreator().getIdentifier()
//		        		.getValue().equals("")  )) {
	          FoafAgent creator = dataset.getCreator();
	          String identificator = creator.getId();
//	          if (identificator == null)
//	        	  identificator = UUID.randomUUID().toString();
	          String idDs = "urn:ngsi-ld:id:" + identificator;     
	          String api = urlCB + "entities/" + idDs;
	          
	          String agentType = "";
	          if (creator.getType() != null) {
	        	  agentType = creator.getType().getValue();
	          }
	        		

	          //int status = restRequest(api, "", "GET");
	          //if (status != 200) {
	            // AGGIUNTA dell'AGENT CREATOR
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

	         // }
	        }
	        // CONTROLLO: durante la sync, se un AGENT PUBLISHER è già presente NON aggiungerlo
//	        if (dataset.getPublisher() != null && (!dataset.getPublisher().getIdentifier()
//	        		.getValue().equals("n.d") || !dataset.getPublisher().getIdentifier()
//	        		.getValue().equals("")  )) {
	        if (dataset.getPublisher() != null) {
	          FoafAgent publisher = dataset.getPublisher();
	          String identificator = publisher.getId();
//	          if (identificator == null)
//	        	  identificator = UUID.randomUUID().toString();
	          String idDs = "urn:ngsi-ld:id:" + identificator;     
	          String api = urlCB + "entities/" + idDs;
	          
	          String agentType = "";
	          if (publisher.getType() != null) {
	        	  agentType = publisher.getType().getValue();
	          }

	          //int status = restRequest(api, "", "GET");
	          //if (status != 200) {
	            // AGGIUNTA dell'AGENT PUBLISHER
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

	          //}
	          
	        }
	        // CONTROLLO: durante la sync, se un AGENT RIGHT HOLDER è già presente NON aggiungerlo
//	        if (dataset.getRightsHolder() != null && (!dataset.getRightsHolder().getIdentifier()
//	        		.getValue().equals("n.d") || !dataset.getRightsHolder().getIdentifier()
//	        		.getValue().equals("")  )) {
	        if (dataset.getRightsHolder() != null) {
	          FoafAgent holder = dataset.getRightsHolder();
	          
	          String identificator = holder.getId();
	          String idDs = "urn:ngsi-ld:id:" + identificator; 
//	          if (identificator == null)
//	        	  identificator = UUID.randomUUID().toString();
	          String api = urlCB + "entities/" + idDs;
	          
	          String agentType = "";
	          if (holder.getType() != null) {
	        	  agentType = holder.getType().getValue();
	          }

	          //int status = restRequest(api, "", "GET");
	          //if (status != 200) {
	            // AGGIUNTA dell'AGENT OUBLISHER
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

	          //}
	        }
	        
	                
	        // DISTRIBUTIONS
	        ArrayList<String> allDistributions = new ArrayList<String>();
	        
	        List<DcatDistribution> distributions = dataset.getDistributions();
	        for (DcatDistribution d : distributions) {

	          String idDistribution = "\"urn:ngsi-ld:Dataset:items:" + d.getId() + "\"";
	          if (!allDistributions.contains(idDistribution)) {
	            allDistributions.add(idDistribution);
	          }

	          String identificat = d.getId();
	          
	          // OTTENGO LA DISTRIBUTION DA IDRA
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
	          String stat = distrib.optString("status", ""); // must be Completed, Deprecated, Under Development, Withdrawn 
	          
	          String idDis = "urn:ngsi-ld:DistributionDCAT-AP:id:" + identificat;
	                
	          String api = urlCB + "entities/" + idDis;
	          //int status = restRequest(api, "", "GET");
	          //if (status != 200) {
	            
	            String typeDis = "DistributionDCAT-AP";
	            
		        String title = titleD.replace("\n", " ");
		        title = title.replace("´", " ");
		        title = title.replace("'", "");
		        title = title.replace("/", " ");
		        title = title.replace("\\", " ");
		        title = title.replace("\"", "");
		        String titleDis = title.replace("\r", " ");

//		        String descrDs = des.replace("\n", " ");
//		        descrDs = descrDs.replace("´", " ");
//		        descrDs = descrDs.replace("'", "");
//		        descrDs = descrDs.replace("/", "");
//		        descrDs = descrDs.replace("\\", " ");
//		        descrDs = descrDs.replace("\"", "");
//		        descrDs = descrDs.replace("•", " ");
//		        descrDs = descrDs.replace("#", "");
//		        descrDs = descrDs.replace("*", "");
//		        descrDs = descrDs.replace("“", "");
//		        descrDs = descrDs.replace("’", "");
//		        String descr = descrDs.replace("\r", " ");

	            d.getNodeId();
	           
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
	            	checksum = d.getChecksum().getChecksumValue().getValue();
	            }

	            // aggiungere dateCreated e dateModified come TIMESTAMP della Entity
	            
	            String dataDis = "{ \"id\": \"" + idDis + "\", \"type\": \"" + typeDis + "\","
//	                + "\"description\": { " 
//	                + "\"type\": \"Property\","
//	                + "\"value\": \"" + descr + "\" }," 
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
	        } // FINE CONTROLLO DISTRIBUTION
	        
	        // CONTROLLO: durante la sync, se un DATASET è già presente NON aggiungerlo
	        //urn:ngsi-ld:Dataset:id:HUZY:68185655
	        String idDs = "urn:ngsi-ld:Dataset:id:" + dataset.getId();     
	        String api = urlCB + "entities/" + idDs;

	        //int status = restRequest(api, "", "GET");
	        //if (status != 200) {
	          // AGGIUNTA DATASET

	          String des = dataset.getDescription().getValue();
	          String descrDs = des.replace("\n", " ");
	          descrDs = descrDs.replace("´", " ");
	          descrDs = descrDs.replace("'", "");
	          descrDs = descrDs.replace("/", " ");
	          descrDs = descrDs.replace("\\", " ");
	          descrDs = descrDs.replace("\"", "");
	          String descr = descrDs.replace("\r", " ");

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
	          for(SkosConceptTheme t: theme) {
	            List<SkosPrefLabel> lab = t.getPrefLabel();
	            for (SkosPrefLabel label: lab)
	            	if(label.getValue() != "")
	            		themes.add("\"" + label.getValue() + "\"");
	          }
//	          if (themes.size() == 0)
//	        	  themes.add("");
	          
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
	          	if(otId.getValue() != "")
	          		otherIdentifier.add("\"" + otId.getValue() + "\"");
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
//	          System.out.println("SOURCE lista: " + dataset.getSource());
//	          System.out.println("SAMPLE lista: " + dataset.getSample());
//	          System.out.println("isVer lista: " + dataset.getIsVersionOf());
//	          System.out.println("hasVer lista: " + dataset.getHasVersion());
//	          System.out.println("relRs lista: " + dataset.getRelatedResource());
	          
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
	          // dateModified Timestamp of the last modification of the entity. This will usually be allocated by the storage platform
	          
	          String typeDs = "Dataset";
	          api = urlCB + "entities/";
	          String dataDs = "{ \"id\": \"" + idDs + "\", \"type\": \"" + typeDs + "\","
	              + "\"description\": { " 							// ricontrolla: datasetDescription?
	              + "\"type\": \"Property\","
	              + "\"value\": \"" + descr + "\" },"
	              + "\"alternateName\": { " 
	              + "\"type\": \"Property\","
	              + "\"value\": \"\" },"
	              + "\"title\": { "
	              + "\"type\": \"Property\","
	              + "\"value\": [ \"" + dataset.getTitle().getValue() + "\" ]" 
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
	              + "\"value\": " + contacts.toString() 		// quando è vuoto è stringa vuota
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
	          
	          //System.out.println("DATA DS:" + dataDs);
	          
	          if (!allEntities.contains(dataDs)) {
	            allEntities.add(dataDs);
	          }

	        //} // FINE CONTROLLO

	      }

	      
	      // CONTROLLO: durante la sync, se il CATALOGUE è già presente NON aggiungerlo
	      int identificator = node.getId();
	      String id = "urn:ngsi-ld:Catalogue:id:" + identificator;
	      String api = urlCB + "entities/" + id;
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
	        
	        String type = "CatalogueDCAT-AP";
	        String data = "{ \"id\": \"" + id + "\", \"type\": \"" + type + "\","
	            + "\"description\": { " 
	            + "\"type\": \"Property\","
	            + "\"value\": \"" + node.getDescription() + "\" }," 
	            + "\"publisher\": { " 
	            + "\"type\": \"Property\","
	            + "\"value\": \"" + node.getPublisherName() + "\" }," 
	            + "\"title\": { "
	            + "\"type\": \"Property\","
	            + "\"value\": [ \"" + node.getName() + "\" ]" 
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

	      //} // FINE CONTROLLO CATALOGUE
	      
	      // CONTROLLO: durante la sync, se l'AGENT DEL CATALOGUE è già presente NON aggiungerlo
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
	        //logger.info(" ----------- AGENT dell'ID " + id +  ": " + data); 
	        
	        if (!allEntities.contains(data)) {
	          allEntities.add(data);
	        }

	      //}
	      // POST CREATE in BATCH
//	      api = urlCB + "entityOperations/create";
//	      status = restRequest(api, allEntities.toString(), "POST");
//	      
//	      if (status != 200 && status != 207 && status != 204 && status != -1 
//	          && status != 201 && status != 301) {
//	        throw new Exception("------------ STATUS CREATE BATCH - ORION: " + status);
//	      }
	      int status = 200;
	      api = urlCB + "entityOperations/create";
	      if (allEntities.size() > 200) {
	    	
	        postRequestWithCheck(allEntities, api, 200);
	        
	      } else {
	    	// POST CREATE BATCH 
	        status = restRequest(api, allEntities.toString(), "POST");
	        System.out.println("STATUS CREATE " + status);
	      }
	   
		
	  return status;
	}
	
	
	
	  /**
	   * Delete a Catalogue from Orion.
	  // * @throws Exception 
	  // * @throws MalformedURLException 
	   */
	  public int deleteCatalogueFromOrion(Configurations config) throws MalformedURLException, Exception {
		  
		  String nodeId = config.getCatalogueId();
		  String urlCB = config.getContextBrokerUrl() + "/ngsi-ld/v1/";
	      
	      // OTTENGO IL CATALOGO
	      RestTemplate restTemplate = new RestTemplate();
	      ResponseEntity<String> result = restTemplate.getForEntity(idraBasePath + "/Idra/api/v1/" 
	      + "/client/catalogues/" + nodeId, String.class); 
	      System.out.println("\n RISULTATO GET CATALOGUE: " + result.getStatusCodeValue());
	      System.out.println("\n CATALOGUE: " + result.getBody());
	      
	      OdmsCatalogue node = restTemplate
	    	      .getForObject(idraBasePath + "/Idra/api/v1/" + "/client/catalogues/" + nodeId, OdmsCatalogue.class);
	      System.out.println("\n CATALOGUE NAME to delete: " + node.getName());

	      // OTTENGO I DATASETS
	      ResponseEntity<String> resultDatasets = restTemplate.getForEntity(idraBasePath + "/Idra/api/v1/" 
	      + "/client/catalogues/" + nodeId + "/datasets", String.class);
	      ObjectMapper objectMapper = new ObjectMapper();
	      JsonNode jsonNode = objectMapper.readTree(resultDatasets.getBody());

	      //String count = objectMapper.readValue(jsonNode.get("count").toString(), String.class);
	      DcatDataset[] datasetsList = objectMapper.readValue(jsonNode.get("results"), DcatDataset[].class);
	      List<DcatDataset> datasets = Arrays.asList(datasetsList);
	      //String s = Integer.toString(datasetsList.size());


	      
	      // ELIMINAZIONE
	      ArrayList<String> listId = new ArrayList<String>();
	  
	      // ELIMINAZIONE CATALOGUE
	      int identificator = node.getId();
	      String id = "\"urn:ngsi-ld:Catalogue:id:" + identificator + "\"";
	      listId.add(id);
	      
	      // ELIMINAZIONE AGENT del CATALOGUE
	      identificator = node.getId();
	      id = "\"urn:ngsi-ld:id:" + identificator + "\"";
	      listId.add(id);
	      
	      // ELIMINAZIONE DATASETS
	      //List<DcatDataset> datasets = MetadataCacheManager.getAllDatasetsByOdmsCatalogue(node.getId());
	      for (DcatDataset dataset : datasets) {   
	        String identif = dataset.getId();
	        String idDs = "\"urn:ngsi-ld:Dataset:id:" + identif + "\"";
	        if (!listId.contains(idDs)) {
	          listId.add(idDs);
	        }
	        
	        //ELIMINAZIONE AGENTS dei DATASETS
	        // 1. CREATORS
	        if (dataset.getCreator() != null) {
	          identif = dataset.getCreator().getId();
	          idDs = "\"urn:ngsi-ld:id:" + identif + "\"";
	          if (!listId.contains(idDs)) {
	            listId.add(idDs);
	          }
	        }
	        // 2. PUBLISHERS
	        if (dataset.getPublisher() != null) {    
	          identif = dataset.getPublisher().getId();
	          idDs = "\"urn:ngsi-ld:id:" + identif + "\"";
	          if (!listId.contains(idDs)) {
	            listId.add(idDs);
	          }
	        }
	        // 3. RIGHT HOLDERS
	        if (dataset.getRightsHolder() != null) {  
	          identif = dataset.getRightsHolder().getId();
	          idDs = "\"urn:ngsi-ld:id:" + identif + "\"";
	          if (!listId.contains(idDs)) {
	            listId.add(idDs);
	          }
	        }
	        
	        //ELIMINAZIONE DISTRIBUTIONS
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
	    	
	        postRequestWithCheck(listId, api, 200);
	        
	      } else {
	    	// POST DELETE BATCH 
	        status = restRequest(api, data, "POST");
	        System.out.println("STATUS DELETE " + status);
	        if (status != 200 && status != 207 && status != 204 && status != -1 
	            && status != 201 && status != 301) {
	          throw new Exception("------------ STATUS DELETE DISTRIBUTION - ORION: " + status);
	        }
	      }
	      return status;
	  }
	  

	  /**
	   * Post Request on Orion with Check on the number of Entities.
	// * @throws Exception exception.
	   * 
	   */
	  public void postRequestWithCheck(List<String> listId, String api, int maxNumberOfEntities) throws Exception {
	    String data =  listId.toString();
	    
	    int r = (listId.size()) % maxNumberOfEntities;
	    int tot = listId.size() - r;
	    int numberOfPost = tot / maxNumberOfEntities;
	    
	    int i = 0;
	    int l = maxNumberOfEntities - 1; // 199
	    for (int k = 0; k < numberOfPost; k++) {
	      List<String> list = listId.subList(i, l + 1);
	      data =  list.toString();
//	      System.out.println("\n NEL FOR, CICLO " + k + " num elem da considerare: " + list.size() + " i = "  + i 
//	    		  + " l= " + l + "\n");
	      //System.out.println("\n DATA:"+data);
	      int status = restRequest(api, data, "POST");
	      if (status != 200 && status != 207 && status != 204 && status != -1 
	          && status != 201 && status != 301) {
	        throw new Exception("------------ STATUS POST - ORION: " + status);
	      }
	      i += maxNumberOfEntities;
	      l += maxNumberOfEntities;
	    }
	  
	    List<String> list = listId.subList(i, (listId.size()));
//	    System.out.println("\n FUORI DAL FOR, num elem da considerare fino all indice " + (listId.size()-1) +  ":" + list.size() + ", i= " + i + "\n");
	    if (list.size() != 0) {
		    data =  list.toString(); 
	
		    int status = restRequest(api, data, "POST");
		    if (status != 200 && status != 207 && status != 204 && status != -1 
		        && status != 201 && status != 301) {
		      throw new Exception("------------ STATUS POST - ORION: " + status);
		    }
	    }
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
		    int status = client.getStatus(response);
		    return status;
	}
	
	
}
