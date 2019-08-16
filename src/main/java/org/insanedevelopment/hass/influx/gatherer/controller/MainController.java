package org.insanedevelopment.hass.influx.gatherer.controller;

import org.insanedevelopment.hass.influx.gatherer.service.InfluxDbSendingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

	@Autowired
	private InfluxDbSendingService service;

	@GetMapping("/")
	public String index() {
		return "index";
	}

	@GetMapping("/web/service/test")
	public String test() {
		service.createStateSnapshotAndSendToInflux();
		return "redirect:/";
	}

	@GetMapping("/web/service/subscribe")
	public String subscribe() {
		service.subscribeToChanges();
		return "redirect:/";
	}

}
