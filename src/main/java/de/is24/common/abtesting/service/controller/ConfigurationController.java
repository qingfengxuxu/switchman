package de.is24.common.abtesting.service.controller;

import com.google.common.collect.Lists;
import de.is24.common.abtesting.service.domain.AbTestConfiguration;
import de.is24.common.abtesting.service.service.AbTestConfigurationService;
import de.is24.common.abtesting.service.service.AbTestDecisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/admin/configurations")
public class ConfigurationController {
  private final AbTestConfigurationService abTestConfigurationService;
  private final AbTestDecisionService abTestDecisionService;

  @Autowired
  public ConfigurationController(AbTestConfigurationService service, AbTestDecisionService abTestDecisionService) {
    this.abTestConfigurationService = service;
    this.abTestDecisionService = abTestDecisionService;
  }

  @RequestMapping("")
  @Secured("ROLE_USER")
  public String index(Model model) {
    Iterable<AbTestConfiguration> allConfigurations = abTestConfigurationService.findAll();
    return getOverviewModelAndView(model, allConfigurations);
  }

  @RequestMapping("/filter")
  @Secured("ROLE_USER")
  public String filterByPrefix(@ModelAttribute(value = "prefix") final String prefix, final Model model) {
    Iterable<AbTestConfiguration> configurations = abTestConfigurationService.findByNamePrefix(prefix);
    return getOverviewModelAndView(model, configurations);
  }

  @RequestMapping("/edit")
  @Secured("ROLE_ADMIN")
  public String edit(@RequestParam(value = "name", required = false) String name, Model model) {
    AbTestConfiguration configuration = abTestConfigurationService.findByName(name);

    return editInternal(configuration, model);
  }

  @RequestMapping(value = "/save", method = RequestMethod.POST)
  @Secured("ROLE_ADMIN")
  public String save(@Valid AbTestConfiguration configuration, BindingResult bindingResult, Model model) {
    if (bindingResult.hasErrors()) {
      return "/admin/configuration/editConfiguration";
    }
    abTestConfigurationService.save(configuration);

    return "redirect:/admin/configurations/";
  }

  @RequestMapping(value = "/save", params = { "addVariant" })
  @Secured("ROLE_ADMIN")
  public String addVariant(AbTestConfiguration configuration, BindingResult bindingResult, Model model) {
    abTestConfigurationService.addVariant(configuration);
    return editInternal(configuration, model);
  }

  @RequestMapping(value = "/save", params = { "removeVariant" })
  @Secured("ROLE_ADMIN")
  public String removeVariant(AbTestConfiguration configuration, BindingResult bindingResult, int removeVariant,
                              Model model) {
    abTestConfigurationService.removeVariant(configuration, removeVariant);
    return editInternal(configuration, model);
  }

  @RequestMapping("/delete/{name}")
  @Secured("ROLE_ADMIN")
  public String delete(@PathVariable String name, @RequestParam Optional<Boolean> deleteDecisions) {
    abTestConfigurationService.delete(name);

    if (deleteDecisions.orElse(false)) {
      abTestDecisionService.deleteByTestName(name);
    }
    return "redirect:/admin/configurations/";
  }

  @RequestMapping("/delete-orphan/{name}")
  @Secured("ROLE_ADMIN")
  public String deleteOrphan(@PathVariable String name) {
    abTestDecisionService.deleteByTestName(name);
    return "redirect:/admin/configurations/";
  }

  private String getOverviewModelAndView(final Model model, final Iterable<AbTestConfiguration> configurations) {
    model.addAttribute("configurations", configurations);
    model.addAttribute("counters", abTestDecisionService.countDecisionsForConfigurations());
    model.addAttribute("lastCreated", abTestDecisionService.latestDecisionForConfigurations());
    model.addAttribute("orphans", abTestDecisionService.findOrphans(toTestNames(configurations)));

    return "admin/configuration/configurations";
  }

  private List<String> toTestNames(Iterable<AbTestConfiguration> allConfigurations) {
    if (allConfigurations == null) {
      return Collections.emptyList();
    }
    return Lists.newArrayList(allConfigurations)
        .stream()
        .map(AbTestConfiguration::getName)
        .collect(Collectors.toList());
  }

  private String editInternal(AbTestConfiguration configuration, Model model) {
    model.addAttribute("abTestConfiguration", (configuration != null) ? configuration : createNewConfiguration());

    return "admin/configuration/editConfiguration";
  }

  private AbTestConfiguration createNewConfiguration() {
    AbTestConfiguration configuration = new AbTestConfiguration();
    abTestConfigurationService.addVariant(configuration);
    return configuration;
  }
}
