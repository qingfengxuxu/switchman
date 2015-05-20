package de.is24.common.abtesting.service.repo;

import de.is24.common.abtesting.service.domain.AbTestConfiguration;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;


public interface AbTestConfigurationRepository extends PagingAndSortingRepository<AbTestConfiguration, String> {
  AbTestConfiguration findByName(@Param("name") String name);
}
