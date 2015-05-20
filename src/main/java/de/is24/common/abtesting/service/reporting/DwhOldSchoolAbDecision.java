package de.is24.common.abtesting.service.reporting;

import de.is24.common.abtesting.service.domain.AbTestDecision;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;


public class DwhOldSchoolAbDecision {
  public static final DateTime NULL_SAFE_TIME = DateTime.parse("02.01.1970", DateTimeFormat.forPattern("dd.MM.yyyy"));

  private final String id;
  private final Long hist_id;
  private final String dml_operation;
  private final Long creation_timestamp;
  private final Long modification_timestamp;
  private final String creation_user;
  private final String modification_user;
  private final String testName;
  private final Integer variant;
  private final String ssoId;

  public static DwhOldSchoolAbDecision from(AbTestDecision abTestDecision, Long historyId, String dmlOperation) {
    return new DwhOldSchoolAbDecision(abTestDecision.getId(),
      historyId,
      dmlOperation,
      (abTestDecision.getCreated() != null) ? abTestDecision.getCreated() : NULL_SAFE_TIME,
      (abTestDecision.getModified() != null) ? abTestDecision.getModified() : NULL_SAFE_TIME,
      (abTestDecision.getCreatedBy() != null) ? abTestDecision.getCreatedBy() : "anonymous",
      abTestDecision.getLastModifiedBy(),
      abTestDecision.getTestName(),
      abTestDecision.getVariantId(),
      abTestDecision.getUserSsoId());
  }

  private DwhOldSchoolAbDecision(String id, Long hist_id, String dml_operation, DateTime creationTimestamp,
                                 DateTime modificationTimestamp, String creation_user, String modificationUser,
                                 String testName, Integer variant, String ssoId) {
    this.id = id;
    this.hist_id = hist_id;
    this.dml_operation = dml_operation;
    this.creation_timestamp = toUnixTimeInSeconds(creationTimestamp);
    this.modification_timestamp = toUnixTimeInSeconds(modificationTimestamp);
    this.creation_user = creation_user;
    this.modification_user = modificationUser;
    this.testName = testName;
    this.variant = variant;
    this.ssoId = ssoId;
  }

  private long toUnixTimeInSeconds(DateTime creationTimestamp) {
    return creationTimestamp.getMillis() / 1000;
  }

  public String getId() {
    return id;
  }

  public Long getHist_id() {
    return hist_id;
  }

  public String getDml_operation() {
    return dml_operation;
  }

  public Long getCreation_timestamp() {
    return creation_timestamp;
  }

  public Long getModification_timestamp() {
    return modification_timestamp;
  }

  public String getCreation_user() {
    return creation_user;
  }

  public String getModification_user() {
    return modification_user;
  }

  public String getTestName() {
    return testName;
  }

  public Integer getVariant() {
    return variant;
  }

  public String getSsoId() {
    return ssoId;
  }
}
