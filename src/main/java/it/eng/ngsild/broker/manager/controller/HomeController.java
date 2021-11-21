package it.eng.ngsild.broker.manager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

	@RequestMapping("/")  
	@ResponseBody 
	public String index() {
		return "The NGSI-LD Broker Manager is running";
	}

}
