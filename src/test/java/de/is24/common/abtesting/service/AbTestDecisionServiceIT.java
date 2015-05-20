package de.is24.common.abtesting.service;

import de.is24.common.abtesting.service.domain.AbTestDecision;
import de.is24.common.abtesting.service.repo.AbTestDecisionRepository;
import de.is24.common.abtesting.service.service.AbTestDecisionService;
import de.is24.common.abtesting.service.service.OrphanedTest;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;


public class AbTestDecisionServiceIT extends RestServiceIT {
  private AbTestDecision oldDecision;
  private AbTestDecision newDecision;
  private static final String TEST_NAME = "AbTestDecisionServiceIT";
  private static final String ORPHANED_TEST_NAME = "OrphanedTest";
  private Map<String, DateTime> latestCreationDatesByTestNames;

  @Autowired
  private AbTestDecisionService abTestDecisionService;

  @Autowired
  private AbTestDecisionRepository abTestDecisionRepository;
  private List<OrphanedTest> orphans;
  private AbTestDecision newOrphanDecision;

  @After
  public void removeCreatedDecisions() {
    abTestDecisionRepository.deleteByTestName(TEST_NAME);
    abTestDecisionRepository.deleteByTestName(ORPHANED_TEST_NAME);
  }

  @Test
  public void shouldDisplayTheLatestDecisionDateCorrectly() {
    givenTwoTestDecisionsWithDifferentCreationDates();
    whenLatestDecisionIsRequested();
    thenCreationDateOfLatestDecisionIsReturned();
  }

  @Test
  public void shouldDisplayOrphans() {
    givenSomeDecisionsForTestNames(TEST_NAME, ORPHANED_TEST_NAME);
    whenFindOrphans();
    thenOrphanedTestIsFound();
  }

  private void whenFindOrphans() {
    orphans = abTestDecisionService.findOrphans(Arrays.asList(TEST_NAME));
  }

  private void givenSomeDecisionsForTestNames(String existingName, String orphanedTestName) {
    abTestDecisionRepository.save(createAbTestDecision(existingName));
    abTestDecisionRepository.save(createAbTestDecision(orphanedTestName));
    newOrphanDecision = abTestDecisionRepository.save(createAbTestDecision(orphanedTestName));
  }

  private void givenTwoTestDecisionsWithDifferentCreationDates() {
    oldDecision = abTestDecisionRepository.save(createAbTestDecision(TEST_NAME));
    newDecision = abTestDecisionRepository.save(createAbTestDecision(TEST_NAME));
  }

  private void whenLatestDecisionIsRequested() {
    latestCreationDatesByTestNames = abTestDecisionService.latestDecisionForConfigurations();
  }

  private void thenCreationDateOfLatestDecisionIsReturned() {
    long latestDecisionCreatedInMillis = latestCreationDatesByTestNames.get(TEST_NAME).getMillis();
    assertThat(latestDecisionCreatedInMillis, not(is(oldDecision.getCreated().getMillis())));
    assertThat(latestDecisionCreatedInMillis, is(newDecision.getCreated().getMillis()));
  }

  private void thenOrphanedTestIsFound() {
    assertThat(orphans, hasSize(1));

    OrphanedTest orphanedTest = orphans.get(0);
    assertThat(orphanedTest.getTestName(), is(ORPHANED_TEST_NAME));
    assertThat(orphanedTest.getCount(), is(2L));
    assertThat(orphanedTest.getLatestCreatedDate().getMillis(), is(newOrphanDecision.getCreated().getMillis()));
  }

  private AbTestDecision createAbTestDecision(String testName) {
    AbTestDecision abTestDecision = new AbTestDecision();
    abTestDecision.setTestName(testName);
    abTestDecision.setUserSsoId(UUID.randomUUID().toString());
    return abTestDecision;
  }

}
