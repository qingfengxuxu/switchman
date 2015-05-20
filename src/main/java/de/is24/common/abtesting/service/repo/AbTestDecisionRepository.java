package de.is24.common.abtesting.service.repo;

import de.is24.common.abtesting.service.domain.AbTestDecision;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import java.util.List;


public interface AbTestDecisionRepository extends PagingAndSortingRepository<AbTestDecision, String> {
  Long deleteByTestName(@Param("testName") String testName);

  AbTestDecision findByTestNameAndUserSsoId(@Param("testName") String testName,
                                            @Param("userSsoId") String userSsoId);

  List<AbTestDecision> findByUserSsoId(@Param("userSsoId") String userSsoId);
}
