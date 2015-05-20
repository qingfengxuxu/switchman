package de.is24.common.abtesting.service.controller;

import org.jsefa.csv.annotation.CsvDataType;
import org.jsefa.csv.annotation.CsvField;


@CsvDataType
public class UploadedDecision {
  @CsvField(pos = 1)
  String testName;

  @CsvField(pos = 2)
  String ssoId;

  @CsvField(pos = 3)
  Integer variant;
}
