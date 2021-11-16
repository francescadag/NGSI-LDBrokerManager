/*******************************************************************************
 * Idra - Open Data Federation Platform
 * Copyright (C) 2021 Engineering Ingegneria Informatica S.p.A.
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/

package it.eng.idra.beans.dcat;
import com.google.gson.annotations.SerializedName;
import it.eng.idra.beans.DistributionAdditionalConfiguration;
//import it.eng.idra.cache.CacheContentType;
import it.eng.idra.utils.CommonUtil;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDFS;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
//import org.apache.solr.common.SolrDocument;
//import org.apache.solr.common.SolrInputDocument;
import org.json.JSONArray;
import org.json.JSONObject;

// TODO: Auto-generated Javadoc
/**
 * Represents a DCAT Distribution.
 *
 * @author
 */

// @Embeddable
public class DcatDistribution implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /** The Constant RDFClass. */
  private static final Resource RDFClass = DCAT.Distribution;

  /** The id. */
  private String id;

  /** The stored rdf. */
  @SerializedName(value = "storedRDF")
  private boolean storedRDF;

  /** The node id. */
  @SerializedName(value = "nodeID")
  // @Column(name = "nodeID")
  private transient String nodeID;

  /**
   * Gets the id.
   *
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * Sets the id.
   *
   * @param id the new id
   */
  public void setId(String id) {
    this.id = id;
  }

  // DCAT FIELDS
  /** The access url. */
  // Mandatory
  @SerializedName(value = "accessURL")
  private DcatProperty accessURL;

  /** The description. */
  // Recommended
  private DcatProperty description;

  /** The format. */
  @JsonIgnore
  private DcatProperty format;

  /** The license. */
  private DctLicenseDocument license;

  /** The byte size. */
  // Optional
  private DcatProperty byteSize;

  /** The checksum. */
  private SpdxChecksum checksum;

  /** The documentation. */
  private List<DcatProperty> documentation;

  /** The download url. */
  @SerializedName(value = "downloadURL")
  @JsonDeserialize()
  private DcatProperty downloadURL;

  /** The language. */
  private List<DcatProperty> language;

  /** The linked schemas. */
  @JsonIgnore
  private List<DctStandard> linkedSchemas;

  /** The media type. */
  private DcatProperty mediaType;

  /** The release date. */
  private DcatProperty releaseDate;

  /** The update date. */
  private DcatProperty updateDate;

  /** The rights. */
  private DcatProperty rights;

  /** The status. */
  private SkosConceptStatus status;

  /** The title. */
  private DcatProperty title;

  /** The has datalets. */
  // private List<Datalet> datalets;
  private boolean hasDatalets = false;

  /** The distribution additional config. */
  private DistributionAdditionalConfiguration distributionAdditionalConfig;

  /**
   * Instantiates a new dcat distribution.
   */
  public DcatDistribution() {
  }

  /**
   * Instantiates a new dcat distribution.
   *
   * @param nodeID the node ID
   */
  /*
   * DON'T TOUCH - CONSTRUCTOR USED BY WEB SCRAPER
   */
  public DcatDistribution(String nodeID) {
    this(nodeID, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
        null, null);
  }

  /**
   * Instantiates a new dcat distribution.
   *
   * @param id            the id
   * @param nodeID        the node ID
   * @param accessURL     the access URL
   * @param description   the description
   * @param format        the format
   * @param license       the license
   * @param byteSize      the byte size
   * @param checksum      the checksum
   * @param documentation the documentation
   * @param downloadURL   the download URL
   * @param language      the language
   * @param linkedSchemas the linked schemas
   * @param mediaType     the media type
   * @param releaseDate   the release date
   * @param updateDate    the update date
   * @param rights        the rights
   * @param status        the status
   * @param title         the title
   * @param hasDatalets   the has datalets
   */
  public DcatDistribution(String id, String nodeID, String accessURL, String description,
      String format, DctLicenseDocument license, String byteSize, SpdxChecksum checksum,
      List<String> documentation, String downloadURL, List<String> language,
      List<DctStandard> linkedSchemas, String mediaType, String releaseDate, String updateDate,
      String rights, SkosConceptStatus status, String title, boolean hasDatalets) {

    this(nodeID, accessURL, description, format, license, byteSize, checksum, documentation,
        downloadURL, language, linkedSchemas, mediaType, releaseDate, updateDate, rights, status,
        title);
    this.setId(id);
    this.setHasDatalets(hasDatalets);
    // setDatalets(datalets);
  }

  /**
   * Instantiates a new dcat distribution.
   *
   * @param nodeID        the node ID
   * @param accessURL     the access URL
   * @param description   the description
   * @param format        the format
   * @param license       the license
   * @param byteSize      the byte size
   * @param checksum      the checksum
   * @param documentation the documentation
   * @param downloadURL   the download URL
   * @param language      the language
   * @param linkedSchemas the linked schemas
   * @param mediaType     the media type
   * @param releaseDate   the release date
   * @param updateDate    the update date
   * @param rights        the rights
   * @param status        the status
   * @param title         the title
   */
  public DcatDistribution(String nodeID, String accessURL, String description, String format,
      DctLicenseDocument license, String byteSize, SpdxChecksum checksum,
      List<String> documentation, String downloadURL, List<String> language,
      List<DctStandard> linkedSchemas, String mediaType, String releaseDate, String updateDate,
      String rights, SkosConceptStatus status, String title) {
    super();
    setNodeID(nodeID);
    setAccessURL2(new DcatProperty(DCAT.accessURL, RDFS.Resource, accessURL));
    setDescription2(new DcatProperty(DCTerms.description, RDFS.Literal, description));
    setDownloadURL2(new DcatProperty(DCAT.downloadURL, RDFS.Resource, downloadURL));
    setFormat2(new DcatProperty(DCTerms.format, DCTerms.MediaTypeOrExtent, format));
    // setLicense(license != null ? license : new
    // DCTLicenseDocument(DCTerms.license.getURI(), "", "", "", nodeID));
    setLicense2(license);
    setByteSize2(new DcatProperty(DCAT.byteSize, RDFS.Literal, byteSize));
    // setChecksum(
    // checksum != null ? checksum : new
    // SPDXChecksum("http://spdx.org/rdf/terms#checksum", "", "", nodeID));
    setChecksum2(checksum);
    setDocumentation2(documentation != null
        ? documentation.stream().map(item -> new DcatProperty(FOAF.page, FOAF.Document, item))
            .collect(Collectors.toList())
        : Arrays.asList(new DcatProperty(FOAF.page, FOAF.Document, "")));
    setLanguage2(language != null
        ? language.stream()
            .map(item -> new DcatProperty(DCTerms.language, DCTerms.LinguisticSystem, item))
            .collect(Collectors.toList())
        : Arrays.asList(new DcatProperty(DCTerms.language, DCTerms.LinguisticSystem, "")));
    setLinkedSchemas(linkedSchemas);
    setMediaType2(new DcatProperty(DCAT.mediaType, DCTerms.MediaType, mediaType));
    setReleaseDate2(new DcatProperty(DCTerms.issued, RDFS.Literal,
        StringUtils.isNotBlank(releaseDate) ? releaseDate : ""));
    setUpdateDate2(new DcatProperty(DCTerms.modified, RDFS.Literal,
        StringUtils.isNotBlank(updateDate) ? updateDate : ""));
    setRights2(new DcatProperty(DCTerms.rights, DCTerms.RightsStatement, rights));
    setStatus2(status);
    setTitle2(new DcatProperty(DCTerms.title, RDFS.Literal, title));
  }

  /**
   * Gets the rdf class.
   *
   * @return the rdf class
   */
  public static Resource getRdfClass() {
    return RDFClass;
  }

  /**
   * Gets the node id.
   *
   * @return the node id
   */
  public String getNodeId() {
    return nodeID;
  }

  /**
   * Sets the node id.
   *
   * @param nodeId the new node id
   */
  public void setNodeID(String nodeId) {
    this.nodeID = nodeId;
  }

  /**
   * Gets the stored rdf.
   *
   * @return the stored rdf
   */
  public boolean getStoredRDF() {
    return storedRDF;
  }

  /**
   * Sets the stored rdf.
   *
   * @param storedRdf the new stored rdf
   */
  public void setStoredRDF(boolean storedRdf) {
    this.storedRDF = storedRdf;
  }

  /**
   * Gets the title.
   *
   * @return the title
   */
  public DcatProperty getTitle() {
    return title;
  }

  /**
   * Sets the title.
   *
   * @param title the new title
   */
  public void setTitle2(DcatProperty title) {
    this.title = title;
  }

  /**
   * Sets the title.
   *
   * @param title the new title
   */
  public void setTitle(String title) {
    setTitle2(new DcatProperty(DCTerms.title, RDFS.Literal, title));
  }

  /**
   * Gets the access url.
   *
   * @return the access url
   */
  public DcatProperty getAccessURL() {
    return accessURL;
  }

  /**
   * Sets the access url.
   *
   * @param accessUrl the new access url
   */
  public void setAccessURL2(DcatProperty accessUrl) {
    this.accessURL = accessUrl;
  }

  /**
   * Sets the access url.
   *
   * @param accessUrl the new access url
   */
  public void setAccessURL(String accessUrl) {
    setAccessURL2(new DcatProperty(DCAT.accessURL, RDFS.Resource, accessUrl));
  }

  /**
   * Gets the description.
   *
   * @return the description
   */
  public DcatProperty getDescription() {
    return description;
  }

  /**
   * Sets the description.
   *
   * @param description the new description
   */
  public void setDescription2(DcatProperty description) {
    this.description = description;
  }

  /**
   * Sets the description.
   *
   * @param description the new description
   */
  public void setDescription(String description) {
    setDescription2(new DcatProperty(DCTerms.description, RDFS.Literal, description));
  }

  /**
   * Gets the media type.
   *
   * @return the media type
   */
  public DcatProperty getMediaType() {
    return mediaType;
  }

  /**
   * Sets the media type.
   *
   * @param mediaType the new media type
   */
  public void setMediaType2(DcatProperty mediaType) {
    this.mediaType = mediaType;
  }

  /**
   * Sets the media type.
   *
   * @param mediaType the new media type
   */
  public void setMediaType(String mediaType) {
    setMediaType2(new DcatProperty(DCAT.mediaType, DCTerms.MediaType, mediaType));
  }

  /**
   * Gets the format.
   *
   * @return the format
   */
  @JsonIgnore
  public DcatProperty getFormat() {
    return format;
  }

  /**
   * Sets the format.
   *
   * @param format the format
   * @return the dcat distribution
   */
  @JsonIgnore
  public DcatDistribution setFormat2(DcatProperty format) {
    if (StringUtils.isBlank(format.getValue())) {
      format.setValue(CommonUtil.extractFormatFromFileExtension(this.getDownloadURL().getValue()));
    }
    this.format = format;
    return this;
  }

  /**
   * Sets the format.
   *
   * @param format the format
   * @return the dcat distribution
   */
  @JsonIgnore
  public DcatDistribution setFormat(String format) {
    if (StringUtils.isBlank(format)) {
      format = CommonUtil.extractFormatFromFileExtension(this.getDownloadURL().getValue());
    }
    return setFormat2(new DcatProperty(DCTerms.format, DCTerms.MediaTypeOrExtent, format));
  }

  /**
   * Gets the license.
   *
   * @return the license
   */
  public DctLicenseDocument getLicense() {
    return license;
  }

  /**
   * Sets the license.
   *
   * @param license the new license
   */
  public void setLicense2(DctLicenseDocument license) {
    this.license = (license != null) ? license
        : new DctLicenseDocument(DCTerms.license.getURI(), "", "", "", nodeID);
  }

  // setLicense(license != null ? license : new
  // DCTLicenseDocument(DCTerms.license.getURI(), "", "", "", nodeID));
  
  protected void setLicense(JsonNode license) throws JsonParseException, JsonMappingException, IOException {
	 
      ObjectMapper objectMapper = new ObjectMapper();
      //String uri = objectMapper.readValue(license.get("uri"), String.class);
      String name = objectMapper.readValue(license.get("name"), String.class);
      String type = objectMapper.readValue(license.get("type"), String.class);
      String versionInfo = objectMapper.readValue(license.get("versionInfo"), String.class);
      setLicense2(new DctLicenseDocument(DCTerms.license.getURI(), name, type, versionInfo, nodeID));
 }

  /**
   * Sets the license uri.
   *
   * @param uri the new license uri
   */
  public void setLicense_uri(String uri) {
    if (license == null) {
      setLicense2(null);
    }
    if (StringUtils.isNotBlank(uri)) {
      license.setUri(uri);
    }
  }

  /**
   * Sets the license name.
   *
   * @param name the new license name
   */
  public void setLicense_name(String name) {
    if (license == null) {
      setLicense2(null);
    }
    if (StringUtils.isNotBlank(name)) {
      license.setName(name);
    }
  }

  /**
   * Sets the license type.
   *
   * @param type the new license type
   */
  public void setLicense_type(String type) {
    if (license == null) {
      setLicense2(null);
    }
    if (StringUtils.isNotBlank(type)) {
      license.setType(type);
    }
  }

  /**
   * Sets the license version info.
   *
   * @param versionInfo the new license version info
   */
  public void setLicense_versionInfo(String versionInfo) {
    if (license == null) {
      setLicense2(null);
    }
    if (StringUtils.isNotBlank(versionInfo)) {
      license.setVersionInfo(versionInfo);
    }
  }

  /**
   * Gets the byte size.
   *
   * @return the byte size
   */
  public DcatProperty getByteSize() {
    return byteSize;
  }

  /**
   * Sets the byte size.
   *
   * @param byteSize the new byte size
   */
  public void setByteSize2(DcatProperty byteSize) {
    this.byteSize = byteSize;
  }

  /**
   * Sets the byte size.
   *
   * @param byteSize the new byte size
   */
  public void setByteSize(String byteSize) {
    setByteSize2(new DcatProperty(DCAT.byteSize, RDFS.Literal, byteSize));
  }

  /**
   * Gets the release date.
   *
   * @return the release date
   */
  public DcatProperty getReleaseDate() {
    return releaseDate;
  }

  /**
   * Sets the release date.
   *
   * @param releaseDate the new release date
   */
  public void setReleaseDate2(DcatProperty releaseDate) {
    this.releaseDate = releaseDate;
  }

  /**
   * Sets the release date.
   *
   * @param releaseDate the new release date
   */
  public void setReleaseDate(String releaseDate) {
    setReleaseDate2(new DcatProperty(DCTerms.issued, RDFS.Literal, releaseDate));
  }

  /**
   * Gets the update date.
   *
   * @return the update date
   */
  public DcatProperty getUpdateDate() {
    return updateDate;
  }

  /**
   * Sets the update date.
   *
   * @param updateDate the new update date
   */
  public void setUpdateDate2(DcatProperty updateDate) {
    this.updateDate = updateDate;
  }

  /**
   * Sets the update date.
   *
   * @param updateDate the new update date
   */
  public void setUpdateDate(String updateDate) {
    setUpdateDate2(new DcatProperty(DCTerms.modified, RDFS.Literal, updateDate));
  }

  // @AttributeOverrides({ @AttributeOverride(name = "value", column =
  // @Column(name = "documentation")) })
  // public List<DCATProperty> getDocumentation() {
  // return documentation;
  // }

  /**
   * Gets the documentation.
   *
   * @return the documentation
   */
  public List<DcatProperty> getDocumentation() {
    return documentation;
  }

  /**
   * Sets the documentation.
   *
   * @param documentation the new documentation
   */
  public void setDocumentation2(List<DcatProperty> documentation) {
    this.documentation = documentation;
  }

  /**
   * Sets the documentation.
   *
   * @param documentation the new documentation
   */
  public void setDocumentation(List<String> documentation) {
	    setDocumentation2(documentation != null
	            ? documentation.stream().map(item -> new DcatProperty(FOAF.page, FOAF.Document, item))
	                .collect(Collectors.toList())
	            : Arrays.asList(new DcatProperty(FOAF.page, FOAF.Document, "")));
  }

  /**
   * Gets the download url.
   *
   * @return the download url
   */
  public DcatProperty getDownloadURL() {
    return downloadURL;
  }

  /**
   * Sets the download url.
   *
   * @param downloadUrl the new download url
   */
  public void setDownloadURL2(DcatProperty downloadUrl) {
    this.downloadURL = downloadUrl;
  }

  /**
   * Sets the download url.
   *
   * @param downloadUrl the new download url
   */
  public void setDownloadURL(String downloadUrl) {
    setDownloadURL2(new DcatProperty(DCAT.downloadURL, RDFS.Resource, downloadUrl));
  }

  /**
   * Gets the language.
   *
   * @return the language
   */
  public List<DcatProperty> getLanguage() {
    return language;
  }

  /**
   * Sets the language.
   *
   * @param language the new language
   */
  public void setLanguage2(List<DcatProperty> language) {
    this.language = language;
  }

  /**
   * Sets the language.
   *
   * @param language the new language
   */
  public void setLanguage(List<String> language) {
	    setLanguage2(language != null
	            ? language.stream()
	                .map(item -> new DcatProperty(DCTerms.language, DCTerms.LinguisticSystem, item))
	                .collect(Collectors.toList())
	            : Arrays.asList(new DcatProperty(DCTerms.language, DCTerms.LinguisticSystem, "")));
	  

  }

  /**
   * Gets the linked schemas.
   *
   * @return the linked schemas
   */
  @JsonIgnore
  public List<DctStandard> getLinkedSchemas() {
    return linkedSchemas;
  }

  /**
   * Sets the linked schemas.
   *
   * @param linkedSchemas the new linked schemas
   */
  @JsonIgnore
  public void setLinkedSchemas(List<DctStandard> linkedSchemas) {
    this.linkedSchemas = linkedSchemas;
  }

  /**
   * Gets the rights.
   *
   * @return the rights
   */
  public DcatProperty getRights() {
    return rights;
  }

  /**
   * Sets the rights.
   *
   * @param rights the new rights
   */
  public void setRights2(DcatProperty rights) {
    this.rights = rights;
  }

  /**
   * Sets the rights.
   *
   * @param rights the new rights
   */
  public void setRights(String rights) {
    setRights2(new DcatProperty(DCTerms.rights, DCTerms.RightsStatement, rights));
  }

  /**
   * Gets the status.
   *
   * @return the status
   */
  public SkosConceptStatus getStatus() {
    return status;
  }

  /**
   * Sets the status.
   *
   * @param status the new status
   */
  public void setStatus2(SkosConceptStatus status) {
    this.status = status;
  }

  /**
   * Sets the status.
   *
   * @param status the new status
   */
  public void setStatus(String status) {
    setStatus2(new SkosConceptStatus("http://www.w3.org/ns/adms#status", "",
        Arrays.asList(new SkosPrefLabel("", status, nodeID)), nodeID));
  }

  /**
   * Gets the checksum.
   *
   * @return the checksum
   */
  public SpdxChecksum getChecksum() {
    return checksum;
  }


  
  /**
   * Sets the checksum.
   *
   * @param checksum the new checksum
   */
  public void setChecksum2(SpdxChecksum checksum) {
	  this.checksum = checksum;
  }
  
  // PROBLEMA: gli arriva un array con uri, algorithm, checksumValue
  public void setChecksum(JsonNode checksum) throws JsonParseException, JsonMappingException, IOException {
	 
      ObjectMapper objectMapper = new ObjectMapper();
      //String uri = objectMapper.readValue(checksum.get("uri"), String.class);
      String algorithm = objectMapper.readValue(checksum.get("algorithm"), String.class);
      String checksumValue = objectMapper.readValue(checksum.get("checksumValue"), String.class);
	  this.checksum = new SpdxChecksum("http://spdx.org/rdf/terms#checksum", algorithm,
			  checksumValue, nodeID);
  }

  // setChecksum(
  // checksum != null ? checksum : new
  // SPDXChecksum("http://spdx.org/rdf/terms#checksum", "", "", nodeID));
 


  /**
   * Checks if is checks for datalets.
   *
   * @return true, if is checks for datalets
   */
  public boolean isHasDatalets() {
    return hasDatalets;
  }

  /**
   * Sets the checks for datalets.
   *
   * @param hasDatalets the new checks for datalets
   */
  public void setHasDatalets(boolean hasDatalets) {
    this.hasDatalets = hasDatalets;
  }

  /**
   * Gets the distribution additional config.
   *
   * @return the distribution additional config
   */
  public DistributionAdditionalConfiguration getDistributionAdditionalConfig() {
    return distributionAdditionalConfig;
  }

  /**
   * Sets the distribution additional config.
   *
   * @param distributionAdditionalConfig the new distribution additional config
   */
  public void setDistributionAdditionalConfig(
      DistributionAdditionalConfiguration distributionAdditionalConfig) {
    this.distributionAdditionalConfig = distributionAdditionalConfig;
  }

  // @LazyCollection(LazyCollectionOption.FALSE)
  // @OneToMany(cascade = { CascadeType.ALL },orphanRemoval=true)
  // @JoinColumns({@JoinColumn(name = "distribution_id",
  // referencedColumnName="id"),
  // @JoinColumn(name = "nodeID", referencedColumnName = "nodeID")})
  // public List<Datalet> getDatalets() {
  // return datalets;
  // }
  //
  // public void setDatalets(List<Datalet> datalets) {
  // this.datalets = datalets;
  // }
  //
  // public void addDatalet(Datalet datalet) {
  // if (this.datalets == null)
  // this.datalets = new ArrayList<Datalet>();
  //
  //
  // if(StringUtils.isBlank(datalet.getTitle())) {
  // Integer newID =1;
  // List<Datalet> tmp = this.datalets.stream().filter(x->
  // x.isCustomTitle()).collect(Collectors.toList());
  // if(tmp.size()!=0)
  // newID = tmp.stream().map(x ->
  // Integer.parseInt(x.getTitle().split("_")[1]))
  // .collect(Collectors.summarizingInt(Integer::intValue)).getMax()+1;
  // datalet.setTitle("Datalet_"+newID);
  // datalet.setCustomTitle(true);
  // }else {
  // datalet.setCustomTitle(false);
  // }
  //
  // this.datalets.add(datalet);
  //
  // }

  /**
   * Checks if is rdf.
   *
   * @return true, if is rdf
   */
  public boolean isRdf() {
    return ((this.format != null && (this.format.getValue().equals("RDF")
        || this.format.getValue().equals("application/rdf+xml")))

        || (this.mediaType != null && (this.mediaType.getValue().equals("RDF")
            || this.mediaType.getValue().equals("application/rdf+xml"))));

  }

  /**
   * To doc.
   *
   * @return the solr input document
   */
//  public SolrInputDocument toDoc() {
//
//    SolrInputDocument doc = new SolrInputDocument();
//    doc.addField("id", id);
//    //doc.addField("content_type", CacheContentType.distribution.toString());
//    doc.addField("nodeID", nodeId);
//    doc.addField("storedRDF", storedRdf);
//    String descTmp = description != null ? description.getValue() : "";
//    try {
//      while (descTmp.getBytes("UTF-8").length >= 32766) {
//        descTmp = descTmp.substring(0, (int) Math.ceil(descTmp.length() * (0.9))).trim();
//      }
//      this.description.setValue(descTmp);
//    } catch (UnsupportedEncodingException e) {
//      e.printStackTrace();
//    }
//
//    doc.addField("accessURL", accessUrl.getValue());
//    doc.addField("description", description.getValue());
//    doc.addField("format", format.getValue());
//
//    // if (license != null)
//    // doc.addChildDocument(license.toDoc(CacheContentType.licenseDocument));
//
//    if (license != null) {
//      try {
//        doc.addField("license", GsonUtil.obj2Json(license, GsonUtil.licenseType));
//      } catch (GsonUtilException e) {
//        e.printStackTrace();
//      }
//    }
//    // if (license != null)
//    // doc.addChildDocument(license.toDoc(CacheContentType.licenseDocument));
//    if (byteSize != null) {
//      doc.addField("byteSize", byteSize.getValue());
//    }
//
//    if (checksum != null) {
//      try {
//        doc.addField("checksum", GsonUtil.obj2Json(checksum, GsonUtil.checksumType));
//        // doc.addField("checksum",checksum.toDoc(CacheContentType.checksum));
//      } catch (GsonUtilException e) {
//        e.printStackTrace();
//      }
//    }
//
//    // if (datalets != null && !datalets.isEmpty()) {
//    // try {
//    // for (Datalet datalet : datalets)
//    // doc.addField("datalets", GsonUtil.obj2Json(datalet, GsonUtil.dataletType));
//    // } catch (GsonUtilException e) {
//    // e.printStackTrace();
//    // }
//    // }
//
//    doc.addField("hasDatalets", hasDatalets);
//
//    // doc.addField("documentation", documentation.getValue());
//    doc.addField("downloadURL", downloadUrl.getValue());
//    // doc.addField("language", language.getValue());
//
//    if (documentation != null && !documentation.isEmpty()) {
//      doc.addField("documentation", documentation.stream().filter(item -> item != null)
//          .map(item -> item.getValue()).collect(Collectors.toList()));
//    }
//    if (language != null && !language.isEmpty()) {
//      doc.addField("language", language.stream().filter(item -> item != null)
//          .map(item -> item.getValue()).collect(Collectors.toList()));
//    }
//
//    if (linkedSchemas != null && !linkedSchemas.isEmpty()) {
//      try {
//        doc.addField("linkedSchemas", GsonUtil.obj2Json(linkedSchemas, GsonUtil.stdListType));
//      } catch (GsonUtilException e) {
//        e.printStackTrace();
//      }
//    }
//    // if (linkedSchemas != null && !linkedSchemas.isEmpty())
//    // linkedSchemas.stream().filter(item -> item != null)
//    // .forEach(item ->
//    // doc.addChildDocument(item.toDoc(CacheContentType.linkedSchemas)));
//    if (mediaType != null) {
//      doc.addField("mediaType", mediaType.getValue());
//    }
//
//    if (releaseDate != null && StringUtils.isNotBlank(releaseDate.getValue())) {
//      doc.addField("releaseDate", releaseDate.getValue());
//    }
//    if (releaseDate != null && StringUtils.isNotBlank(updateDate.getValue())) {
//      doc.addField("updateDate", updateDate.getValue());
//    }
//    if (rights != null) {
//      doc.addField("rights", rights.getValue());
//    }
//    if (title != null) {
//      doc.addField("title", title.getValue());
//    }
//
//    if (status != null) {
//      try {
//        doc.addField("status", GsonUtil.obj2Json(status, GsonUtil.conceptType));
//      } catch (GsonUtilException e) {
//        e.printStackTrace();
//      }
//    }
//
//    return doc;
//
//  }
//
//  /**
//   * Doc to DCAT distribution.
//   *
//   * @param doc the doc
//   * @return the dcat distribution
//   */
//  public static DcatDistribution docToDcatDistribution(SolrDocument doc) {
//
//    String nodeIdentifier = doc.getFieldValue("nodeID").toString();
//    String distribIssued = doc.getOrDefault("releaseDate", "").toString();
//    if (StringUtils.isNotBlank(distribIssued)) {
//      distribIssued = CommonUtil.toUtcDate(distribIssued);
//    }
//    String distribModified = doc.getOrDefault("updateDate", "").toString();
//    if (StringUtils.isNotBlank(distribModified)) {
//      distribModified = CommonUtil.toUtcDate(distribModified);
//    }
//
//    List<SolrDocument> childDocs = doc.getChildDocuments();
//    DctLicenseDocument license = null;
//    SpdxChecksum checksum = null;
//    List<DctStandard> linkedSchemas = new ArrayList<DctStandard>();
//    SkosConceptStatus status = null;
//    // List<Datalet> datalets = new ArrayList<Datalet>();
//    if (null != childDocs) {
//
//      for (SolrDocument child : childDocs) {
//
//        //if (child.containsKey("content_type") && child.getFieldValue("content_type")
//        //    .equals(CacheContentType.licenseDocument.toString())) {
//        //  license = DctLicenseDocument.docToDctLicenseDocument(child, nodeIdentifier);
//        //}
//
//        // if (child.containsKey("content_type")
//        // &&
//        // child.getFieldValue("content_type").equals(CacheContentType.linkedSchemas.toString()))
//        // {
//        // linkedSchemas.add(DCTStandard.docToDCATStandard(child, nodeID));
//        // }
//
////        if (child.containsKey("content_type")
////            && child.getFieldValue("content_type").equals(CacheContentType.checksum.toString())) {
////          checksum = SpdxChecksum.docToSpdxChecksum(child, "http://spdx.org/rdf/terms#checksum",
////              nodeIdentifier);
////        }
//
//      }
//    }
//
//    if (doc.getFieldValue("checksum") != null) {
//      checksum = SpdxChecksum.jsonToSpdxChecksum(
//          new JSONObject(doc.getFieldValue("checksum").toString()),
//          "http://spdx.org/rdf/terms#checksum", nodeIdentifier);
//    }
//
//    if (doc.getFieldValue("license") != null) {
//      license = DctLicenseDocument.jsonToDctLicenseDocument(
//          new JSONObject(doc.getFieldValue("license").toString()), nodeIdentifier);
//    }
//
//    if (doc.getFieldValue("status") != null) {
//      status = SkosConceptStatus.jsonToSkosConcept(
//          new JSONObject(doc.getFieldValue("status").toString()),
//          "http://www.w3.org/ns/adms#status", nodeIdentifier);
//    }
//
//    // try {
//    // license = GsonUtil.json2Obj(doc.getFieldValue("license").toString(),
//    // GsonUtil.licenseType);
//    // } catch (GsonUtilException e) {
//    // e.printStackTrace();
//    // }
//
//    if (doc.getFieldValue("linkedSchemas") != null) {
//      linkedSchemas = DctStandard.jsonArrayToDcatStandardList(
//          new JSONArray(doc.getFieldValue("linkedSchemas").toString()), nodeIdentifier);
//    }
//
//    // try {
//    // linkedSchemas =
//    // GsonUtil.json2Obj(doc.getFieldValue("linkedSchemas").toString(),
//    // GsonUtil.stdListType);
//    // } catch (GsonUtilException e) {
//    // e.printStackTrace();
//    // }
//
//    // if (doc.getFieldValue("datalets") != null) {
//    // try {
//    // List<String> dataletsString = (List<String>) doc.getFieldValue("datalets");
//    // for (String s : dataletsString)
//    // datalets.add(GsonUtil.json2Obj(s, GsonUtil.dataletType));
//    // } catch (GsonUtilException e) {
//    // e.printStackTrace();
//    // }
//    // }
//    String byteSize = "";
//    if (doc.getFieldValue("byteSize") != null) {
//      byteSize = doc.getFieldValue("byteSize").toString();
//    }
//
//    DcatDistribution distr = new DcatDistribution(doc.getFieldValue("id").toString(),
//        doc.getFieldValue("nodeID").toString(), doc.getFieldValue("accessURL").toString(),
//        doc.getFieldValue("description").toString(), doc.getFieldValue("format").toString(),
//        license, byteSize, checksum, (ArrayList<String>) doc.getFieldValue("documentation"),
//        doc.getFieldValue("downloadURL").toString(),
//        (ArrayList<String>) doc.getFieldValue("language"), linkedSchemas,
//        (doc.getFieldValue("mediaType") != null) ? doc.getFieldValue("mediaType").toString() : "",
//        distribIssued, distribModified,
//        (doc.getFieldValue("rights") != null) ? doc.getFieldValue("rights").toString() : "", status,
//        doc.getFieldValue("title").toString(), (Boolean) doc.getFieldValue("hasDatalets"));
//    // datalets);
//    distr.setStoredRdf((Boolean) doc.getFieldValue("storedRDF"));
//    return distr;
//  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "DCATDistribution [id=" + id + ", datalets= " + hasDatalets + "]";
  }

}
