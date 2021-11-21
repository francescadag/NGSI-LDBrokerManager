package it.eng.ngsild.broker.manager.controller.api;

import javax.validation.Valid;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import it.eng.idra.beans.ErrorResponse;
import it.eng.ngsild.broker.manager.model.Configurations;
import it.eng.ngsild.broker.manager.service.CatalogueService;

@RestController
public class BrokerManagerController {
	
    /** The logger. */
	private static Logger logger = LogManager.getLogger(BrokerManagerController.class);

	@Autowired
	private CatalogueService cs;

	public BrokerManagerController() {
		cs = new CatalogueService(); 
	}
	
	// ADDING CATALOGUE, POST
	@RequestMapping(value="/startProcess", method=RequestMethod.POST)  
	@CrossOrigin(origins = {"${idra.basepath}"})
	public Response start(@Valid @RequestBody Configurations config) {
		try {
			logger.info("Broker Manager for ADDING RICEVE the Node ID: " + config.getCatalogueId());
			logger.info("and the Context broker URL: " + config.getContextBrokerUrl());

			int status = cs.start(config);
			return Response.status(status).build();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return handleErrorResponse500(e);
		}
	}
	
	// DELETING CATALOGUE, POST
	@RequestMapping(value="/deleteCatalogue", method=RequestMethod.POST)  
	@CrossOrigin(origins = {"${idra.basepath}"})
	public Response delete(@Valid @RequestBody Configurations config) {
		try {
			logger.info("Broker Manager for DELETING RICEVE the Node ID: " + config.getCatalogueId());
			logger.info("and the Context broker URL: " + config.getContextBrokerUrl());
			
			int status = cs.delete(config);
			return Response.status(status).build();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return handleErrorResponse500(e);
		}
	}
	
	  /**
	   * Handle error response 500.
	   *
	   * @param e the e
	   * @return the response
	   */
	  private static Response handleErrorResponse500(Exception e) {
	    e.printStackTrace();
	    ErrorResponse error = new ErrorResponse(
	        String.valueOf(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()), e.getMessage(),
	        e.getClass().getSimpleName(), "An error occurred, please contact the administrator!");
	    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_JSON)
	        .entity(error.toJson()).build();
	  }

}
