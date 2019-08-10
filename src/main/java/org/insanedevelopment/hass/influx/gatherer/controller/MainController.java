package org.insanedevelopment.hass.influx.gatherer.controller;

import org.insanedevelopment.hass.influx.gatherer.repository.HassioRestStateClientImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

	@Autowired
	private HassioRestStateClientImpl repository;

	@GetMapping("/")
	public String index() {
		return "index";
	}

	@GetMapping("/web/service/test")
	public String test() {
		repository.getAllCurrentStates();
		repository.subscribeToChanges();
		return "redirect:/";
	}
}
