package de.is24.common.abtesting.service.service;

import org.joda.time.DateTime;


public class OrphanedTest {
  private String testName;
  private long count;
  private DateTime latestCreatedDate;

  public OrphanedTest(String testName, long count, DateTime latestCreatedDate) {
    this.testName = testName;
    this.count = count;
    this.latestCreatedDate = latestCreatedDate;
  }

  public String getTestName() {
    return testName;
  }

  public void setTestName(String testName) {
    this.testName = testName;
  }

  public long getCount() {
    return count;
  }

  public void setCount(long count) {
    this.count = count;
  }

  public DateTime getLatestCreatedDate() {
    return latestCreatedDate;
  }

  public void setLatestCreatedDate(DateTime latestCreatedDate) {
    this.latestCreatedDate = latestCreatedDate;
  }
}
