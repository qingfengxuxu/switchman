package de.is24.common.abtesting.service.controller;

import de.is24.common.abtesting.service.domain.AbTestConfiguration;
import de.is24.common.abtesting.service.service.AbTestConfigurationService;
import de.is24.common.abtesting.service.service.AbTestDecisionService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import java.util.Optional;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class ConfigurationControllerTest {
  public static final String NAME = "abTestConfigurationName";
  public static final int REMOVE_VARIANT = 1;
  @Mock
  private AbTestConfigurationService service;
  @Mock
  private AbTestDecisionService abTestDecisionService;
  @Mock
  private Model model;
  @Mock
  private BindingResult bindingResult;
  private ConfigurationController controller;
  private AbTestConfiguration configuration;

  @Before
  public void setUp() {
    controller = new ConfigurationController(service, abTestDecisionService);
    configuration = new AbTestConfiguration();
  }


  @Test
  public void returnsIndexPageWithAllConfigurations() {
    assertThat(controller.index(model), equalTo("admin/configuration/configurations"));
    verify(service).findAll();
    verify(model).addAttribute(eq("configurations"), anyCollectionOf(AbTestConfiguration.class));
    verify(model).addAttribute(eq("counters"), anyMapOf(String.class, Long.class));
  }


  @Test
  public void returnsEditPageWithConfiguration() {
    when(service.findByName(NAME)).thenReturn(configuration);

    assertThat(controller.edit(NAME, model), equalTo("admin/configuration/editConfiguration"));
    verify(service).findByName(NAME);
    verify(model).addAttribute(eq("abTestConfiguration"), same(configuration));
  }

  @Test
  public void savesConfigurationAndRedirectToIndexPage() {
    when(bindingResult.hasErrors()).thenReturn(false);
    assertThat(controller.save(configuration, bindingResult, model), equalTo("redirect:/admin/configurations/"));
    verify(service).save(configuration);
  }

  @Test
  public void continueEditingWhenValidationFails() {
    when(bindingResult.hasErrors()).thenReturn(true);
    assertThat(controller.save(configuration, bindingResult, model), equalTo("/admin/configuration/editConfiguration"));
    verify(service, never()).save(any(AbTestConfiguration.class));
  }

  @Test
  public void addsVariantAndContinueEditing() {
    assertThat(controller.addVariant(configuration, bindingResult, model),
      equalTo("admin/configuration/editConfiguration"));
    verify(service).addVariant(configuration);
    verify(model).addAttribute(eq("abTestConfiguration"), same(configuration));
  }

  @Test
  public void removesVariantAndContinueEditing() {
    assertThat(controller.removeVariant(configuration, bindingResult, REMOVE_VARIANT, model),
      equalTo("admin/configuration/editConfiguration"));
    verify(service).removeVariant(configuration, REMOVE_VARIANT);
    verify(model).addAttribute(eq("abTestConfiguration"), same(configuration));
  }

  @Test
  public void deletesConfigurationAndRedirectToIndexPage() {
    assertThat(controller.delete(NAME, Optional.<Boolean>empty()), equalTo("redirect:/admin/configurations/"));
    verify(service).delete(NAME);
  }

}
