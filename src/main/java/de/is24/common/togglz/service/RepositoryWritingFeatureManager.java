package de.is24.common.togglz.service;

import de.is24.common.infrastructure.security.CurrentClientNameProvider;
import de.is24.common.togglz.remote.api.RemoteFeature;
import de.is24.common.togglz.remote.api.RemoteFeatureState;
import de.is24.common.togglz.service.repo.FeatureStateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.togglz.core.Feature;
import org.togglz.core.manager.FeatureManager;
import org.togglz.core.metadata.FeatureMetaData;
import org.togglz.core.repository.FeatureState;
import org.togglz.core.user.FeatureUser;
import org.togglz.core.user.SimpleFeatureUser;

import java.util.HashSet;
import java.util.Set;


@Component
public class RepositoryWritingFeatureManager implements FeatureManager {
  
  @Autowired
  private FeatureStateRepository featureStateRepository;
  
  @Override
  public String getName() {
    return "RepositoryWritingFeatureManager";
  }

  @Override
  public Set<Feature> getFeatures() {
    Set<Feature> features = new HashSet<>();
    featureStateRepository.findAll().forEach(s -> features.add(s.getFeature()));
    return features;
  }

  @Override
  public FeatureMetaData getMetaData(Feature feature) {
    if (feature instanceof RemoteFeature) {
      return (RemoteFeature)feature;
    }
    return null;
  }

  @Override
  public boolean isActive(Feature feature) {
    return featureStateRepository.findByFeatureName(feature.name()).getEnabled();
  }

  @Override
  public FeatureUser getCurrentFeatureUser() {
    return new SimpleFeatureUser(CurrentClientNameProvider.getCurrentClientName(), true);
  }

  @Override
  public FeatureState getFeatureState(Feature feature) {
    return RemoteFeatureState.toFeatureState(featureStateRepository.findByFeatureName(feature.name()));
  }

  @Override
  public void setFeatureState(FeatureState state) {
    de.is24.common.togglz.service.domain.FeatureState featureStateFromRepo = featureStateRepository.findByFeatureName(state.getFeature().name());
    RemoteFeatureState remoteFeatureState = RemoteFeatureState.from(state);
    if (featureStateFromRepo != null) {
      featureStateFromRepo.setFeature(remoteFeatureState.getFeature());
      featureStateFromRepo.setEnabled(remoteFeatureState.getEnabled());
      featureStateFromRepo.setStrategyId(remoteFeatureState.getStrategyId());
      featureStateFromRepo.setParameters(remoteFeatureState.getParameters());
      featureStateRepository.save(featureStateFromRepo);
    } else {
      featureStateRepository.save((de.is24.common.togglz.service.domain.FeatureState) remoteFeatureState);
    }
  }
}
