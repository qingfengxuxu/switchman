package de.is24.common.abtesting.service.repo;

import de.is24.common.abtesting.service.domain.AbTestConfiguration;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface AbTestConfigurationRepository extends PagingAndSortingRepository<AbTestConfiguration, String> {
  AbTestConfiguration findByName(@Param("name") String name);

  List<AbTestConfiguration> findByNameStartsWith(@Param("prefix") String prefix, Pageable pageable);
}
