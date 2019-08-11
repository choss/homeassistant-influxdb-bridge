package org.insanedevelopment.hass.influx.gatherer.controller;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.insanedevelopment.hass.influx.gatherer.model.FilterPathSpec;
import org.insanedevelopment.hass.influx.gatherer.repository.ConfigRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ConfigController {

	@Autowired
	private ConfigRepositoryImpl configRepository;

	@GetMapping("/web/config/list")
	public String showConfigItems(Model model) {
		List<FilterPathSpec> result = configRepository.getAllConfigElements();
		model.addAttribute("result", result);
		return "configItemsPage";
	}

	@GetMapping("/web/config/delete/{id}")
	public String deleteItem(@PathVariable("id") int position) {
		configRepository.deleteItem(position);
		return "redirect:/web/config/list";
	}

	@PostMapping("/web/config/add")
	public String addItem(@ModelAttribute("path") String path,
			@ModelAttribute("exportState") String exportState,
			@ModelAttribute("exportAttributes") String exportAttributes,
			@ModelAttribute("stateField") String stateField,
			@ModelAttribute("tagAttributes") String tagAttributes) {

		List<String> exportAttributesList = Arrays.asList(StringUtils.split(exportAttributes, ",")).stream().map(s -> s.trim()).collect(Collectors.toList());
		List<String> tagAttributesList = Arrays.asList(StringUtils.split(tagAttributes, ",")).stream().map(s -> s.trim()).collect(Collectors.toList());

		FilterPathSpec spec = new FilterPathSpec(path);
		spec.setReportState("on".equals(exportState));
		spec.setOverwriteStateFieldWithAttribute(stateField);

		spec.setTagAttributes(tagAttributesList);
		spec.setReportAttributes(exportAttributesList);

		configRepository.addItem(spec);
		return "redirect:/web/config/list";
	}
}
