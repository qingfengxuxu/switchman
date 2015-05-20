package de.is24.common.abtesting.service.service;

import com.google.common.collect.Maps;
import de.is24.common.abtesting.service.domain.AbTestConfiguration;
import de.is24.common.abtesting.remote.api.AbTestVariant;
import de.is24.common.abtesting.service.repo.AbTestConfigurationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;


@Service
public class AbTestConfigurationService {
  private AbTestConfigurationRepository repository;

  @Autowired
  public AbTestConfigurationService(AbTestConfigurationRepository repository) {
    this.repository = repository;
  }

  public Iterable<AbTestConfiguration> findAll() {
    return repository.findAll();
  }

  public AbTestConfiguration findByName(String name) {
    return repository.findByName(name);
  }

  public AbTestConfiguration save(AbTestConfiguration configuration) {
    return repository.save(configuration);
  }

  public void delete(String name) {
    repository.delete(name);
  }

  public Map<String, AbTestConfiguration> getConfigurationMap() {
    Map<String, AbTestConfiguration> map = Maps.newHashMap();
    for (AbTestConfiguration abTestConfiguration : findAll()) {
      map.put(abTestConfiguration.getName(), abTestConfiguration);
    }

    return map;
  }

  public void removeVariant(AbTestConfiguration configuration, int removeVariant) {
    configuration.getVariants().remove(removeVariant);
    normalizeVariantIds(configuration);
  }

  public void addVariant(AbTestConfiguration configuration) {
    configuration.getVariants().add(new AbTestVariant());
    normalizeVariantIds(configuration);
  }

  private void normalizeVariantIds(AbTestConfiguration configuration) {
    for (int i = 0; i < configuration.getVariants().size(); i++) {
      configuration.getVariants().get(i).setId(i);
    }
  }
}
