package it.eng.ngsild.broker.manager.controller.api;

import javax.validation.Valid;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

	@Autowired
	private CatalogueService cs;

	public BrokerManagerController() {
		cs = new CatalogueService(); 
	}
	
	// AGGIUNTA CATALOGO, POST
	@RequestMapping(value="/startProcess", method=RequestMethod.POST)  
	@CrossOrigin(origins = {"${idra.basepath}"})
	public Response start(@Valid @RequestBody Configurations config) {
		try {
			System.out.println("\n\n ----- Il Broker Manager per ADD RICEVE l'ID: " + config.getCatalogueId());
			System.out.println(" ----- e il Context broker Url: " + config.getContextBrokerUrl());

			int status = cs.start(config);
			
	        if (status != 200 && status != 207 && status != 204 && status != -1 
		            && status != 201 && status != 301) {
		          throw new Exception("------------ STATUS START - BROKER MANAGER: " + status);
		    } else {
				return Response.status(Response.Status.OK).build();
		    }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return handleErrorResponse500(e);
		}
	}
	
	// CANCELLAZIONE CATALOGO, POST
	@RequestMapping(value="/deleteCatalogue", method=RequestMethod.POST)  
	@CrossOrigin(origins = "http://localhost:8080")
	public Response delete(@Valid @RequestBody Configurations catalogueId) {
		try {
			System.out.println("\n\n ----- Il Broker Manager per DELETE RICEVE l'ID: " + catalogueId.getCatalogueId());
			int status = cs.delete(catalogueId);

	        if (status != 200 && status != 207 && status != 204 && status != -1 
		            && status != 201 && status != 301) {
		          throw new Exception("------------ STATUS DELETE - ORION MANAGER: " + status);
		    } else {
				return Response.status(Response.Status.OK).build();
		    }
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
