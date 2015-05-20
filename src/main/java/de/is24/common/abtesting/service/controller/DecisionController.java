package de.is24.common.abtesting.service.controller;

import com.google.common.collect.Lists;
import de.is24.common.abtesting.service.domain.AbTestDecision;
import de.is24.common.abtesting.service.service.AbTestConfigurationService;
import de.is24.common.abtesting.service.service.AbTestDecisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;


@Controller
@RequestMapping("/admin/decisions")
public class DecisionController {
  private final AbTestDecisionService decisionService;
  private final AbTestConfigurationService configurationService;


  @Autowired
  public DecisionController(AbTestDecisionService decisionService, AbTestConfigurationService configurationService) {
    this.decisionService = decisionService;
    this.configurationService = configurationService;
  }

  @RequestMapping("/")
  public String index() {
    return "admin/decision/search";
  }


  @RequestMapping(value = "/decisions")
  @Secured("ROLE_ADMIN")
  public String search(String userSsoId, Model model) {
    model.addAttribute("userSsoId", userSsoId);

    List<AbTestDecision> decisions = decisionService.findBySsoId(userSsoId);
    model.addAttribute("decisions", decisions);
    model.addAttribute("configurations", configurationService.getConfigurationMap());
    return "admin/decision/decisions";
  }

  @RequestMapping(value = "/decisionsFromUpload")
  @Secured("ROLE_ADMIN")
  public String search(String decisions, int unused, Model model) {
    String[] splitted = decisions.split(",");
    Iterable<AbTestDecision> decisionIds = decisionService.findByDecisionIds(Lists.newArrayList(splitted));
    model.addAttribute("decisions", decisionIds);
    model.addAttribute("configurations", configurationService.getConfigurationMap());
    return "admin/decision/decisions";
  }

  @RequestMapping(value = "/update", method = RequestMethod.POST)
  @Secured("ROLE_ADMIN")
  public String update(String userSsoId, String testName, Integer variantId, RedirectAttributes redirectAttributes) {
    decisionService.update(userSsoId, testName, variantId);
    redirectAttributes.addAttribute("userSsoId", userSsoId);
    return "redirect:/admin/decisions/decisions";
  }


  @RequestMapping(value = "/delete/{userSsoId}/{testName}")
  @Secured("ROLE_ADMIN")
  public String delete(@PathVariable String userSsoId, @PathVariable String testName,
                       RedirectAttributes redirectAttributes) {
    decisionService.delete(userSsoId, testName);
    redirectAttributes.addAttribute("userSsoId", userSsoId);
    return "redirect:/admin/decisions/decisions";
  }

}
