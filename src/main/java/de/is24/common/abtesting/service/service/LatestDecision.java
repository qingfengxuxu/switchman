package de.is24.common.abtesting.service.service;

import org.joda.time.DateTime;


public class LatestDecision {
  private DateTime latestDecision;
  private String testName;

  public LatestDecision(String testName, DateTime latestDecision) {
    this.latestDecision = latestDecision;
    this.testName = testName;
  }

  public DateTime getLatestDecision() {
    return latestDecision;
  }

  public void setLatestDecision(DateTime latestDecision) {
    this.latestDecision = latestDecision;
  }

  public String getTestName() {
    return testName;
  }

  public void setTestName(String testName) {
    this.testName = testName;
  }
}
