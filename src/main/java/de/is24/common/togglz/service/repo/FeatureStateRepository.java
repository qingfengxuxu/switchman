package de.is24.common.togglz.service.repo;

import de.is24.common.togglz.service.domain.FeatureState;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;


public interface FeatureStateRepository extends PagingAndSortingRepository<FeatureState, String> {
  FeatureState findByFeatureName(@Param("name") String name);
}
