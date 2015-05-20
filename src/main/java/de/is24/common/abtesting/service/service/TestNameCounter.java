package de.is24.common.abtesting.service.service;


public class TestNameCounter {
  private Long count;
  private String testName;

  public TestNameCounter(String testName, long count) {
    this.testName = testName;
    this.count = count;
  }

  public Long getCount() {
    return count;
  }

  public void setCount(Long count) {
    this.count = count;
  }

  public String getTestName() {
    return testName;
  }

  public void setTestName(String testName) {
    this.testName = testName;
  }
}
