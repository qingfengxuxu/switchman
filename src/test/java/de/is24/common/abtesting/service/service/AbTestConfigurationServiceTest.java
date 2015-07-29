package de.is24.common.abtesting.service.service;

import de.is24.common.abtesting.service.domain.AbTestConfiguration;
import de.is24.common.abtesting.service.repo.AbTestConfigurationRepository;
import org.hamcrest.core.IsSame;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class AbTestConfigurationServiceTest {
  public static final String NAME = "abTestConfigurationName";
  public static final String DESCRIPTION = "description";
  @Mock
  private AbTestConfigurationRepository repository;

  private AbTestConfigurationService service;
  private AbTestConfiguration configuration;

  @Before
  public void setUp() {
    service = new AbTestConfigurationService(repository);
    configuration = new AbTestConfiguration();
  }

  @Test
  public void delegatesSaveToRepository() {
    service.save(configuration);
    verify(repository).save(configuration);
  }

  @Test
  public void delegatesDeleteToRepository() {
    service.delete(NAME);
    verify(repository).delete(NAME);
  }

  @Test
  public void delegatesFindAllToRepository() {
    List<AbTestConfiguration> abTestConfigurations = Collections.<AbTestConfiguration>emptyList();
    when(repository.findAll()).thenReturn(abTestConfigurations);
    assertThat(service.findAll(), IsSame.<Iterable<AbTestConfiguration>>sameInstance(abTestConfigurations));
  }

  @Test
  public void delegatesFindByNameToRepository() {
    when(repository.findByName(NAME)).thenReturn(configuration);
    assertThat(service.findByName(NAME), IsSame.<AbTestConfiguration>sameInstance(configuration));
  }

  @Test
  public void delegatesFindByNamePrefixToRepository() {
    final List<AbTestConfiguration> abTestConfigurations = newArrayList(configuration);
    when(repository.findByNameStartsWith(NAME)).thenReturn(abTestConfigurations);
    assertThat(service.findByNamePrefix(NAME), IsSame.sameInstance(abTestConfigurations));
    verify(repository).findByNameStartsWith(NAME);
  }

  @Test
  public void addsNewVariantToConfiguration() {
    service.addVariant(configuration);
    assertThat(configuration.getVariants(), hasSize(1));
    assertVariant(0, 0, null);

    configuration.getVariants().get(0).setDescription(DESCRIPTION);

    service.addVariant(configuration);
    assertThat(configuration.getVariants(), hasSize(2));
    assertVariant(0, 0, DESCRIPTION);
    assertVariant(1, 1, null);

  }

  @Test
  public void removedVariantFromConfiguration() {
    service.addVariant(configuration);
    service.addVariant(configuration);
    service.addVariant(configuration);

    configuration.getVariants().get(1).setDescription(DESCRIPTION);

    assertThat(configuration.getVariants(), hasSize(3));
    assertVariant(0, 0, null);
    assertVariant(1, 1, DESCRIPTION);
    assertVariant(2, 2, null);

    service.removeVariant(configuration, 1);

    assertThat(configuration.getVariants(), hasSize(2));
    assertVariant(0, 0, null);
    assertVariant(1, 1, null);
  }

  private void assertVariant(int index, int id, Object description) {
    assertThat(configuration.getVariants().get(index).getId(), equalTo(id));
    assertThat(configuration.getVariants().get(index).getDescription(), equalTo(description));
  }

}
