package de.is24.common.togglz.service.controller;

import de.is24.common.togglz.remote.api.RemoteFeature;
import de.is24.common.togglz.service.domain.FeatureState;
import de.is24.common.togglz.service.repo.FeatureStateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;

@Controller
@RequestMapping("/admin/togglz")
public class TogglzConfigurationController {
  
  private final FeatureStateRepository featureStateRepository;

  @Autowired
  public TogglzConfigurationController(FeatureStateRepository featureStateRepository) {
    this.featureStateRepository = featureStateRepository;
  }

  @RequestMapping("/")
  @Secured("ROLE_USER")
  public String index(Model model) {
    Set<RemoteFeature> remoteFeatures = new HashSet<>();
    featureStateRepository.findAll().forEach(s -> remoteFeatures.add(s.getFeature()));
    model.addAttribute(remoteFeatures);
    return "admin/togglz/features";
  }

  @RequestMapping("/edit")
  @Secured("ROLE_ADMIN")
  public String edit(@RequestParam(value = "name", required = false) String name, Model model) {
    FeatureState featureState = featureStateRepository.findByFeatureName(name);
    RemoteFeature feature = null;
    if(featureState != null) {
      feature = featureState.getFeature();
    }
    model.addAttribute(feature != null ? feature : new RemoteFeature());
    return "/admin/togglz/edit";
  }

  @RequestMapping(value = "/save", method = RequestMethod.POST)
  @Secured("ROLE_ADMIN")
  public String save(@Valid RemoteFeature featureSwitch, BindingResult bindingResult, Model model) {
    if (bindingResult.hasErrors()) {
      return "/admin/togglz/edit";
    }

    FeatureState state = featureStateRepository.findByFeatureName(featureSwitch.name());
    if (state != null) {
      state.setFeature(featureSwitch);
    } else {
      state = new FeatureState();
      state.setFeature(featureSwitch);
      state.setEnabled(featureSwitch.getEnabledByDefault());
    }
    featureStateRepository.save(state);

    return "redirect:/admin/togglz/";
  }

  @RequestMapping("/delete/{name}")
  @Secured("ROLE_ADMIN")
  public String delete(@PathVariable String name) {
    FeatureState featureState = featureStateRepository.findByFeatureName(name);
    if (featureState != null) {
      featureStateRepository.delete(featureState);
    }
    return "redirect:/admin/togglz/";
  }
  
}
