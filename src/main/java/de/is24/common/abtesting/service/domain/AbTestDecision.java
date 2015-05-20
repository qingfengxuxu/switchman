package de.is24.common.abtesting.service.domain;

import de.is24.common.infrastructure.domain.DomainObject;
import lombok.Data;
import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;


@CompoundIndexes(
  {
    @CompoundIndex(name = "userSsoId_testName_idx", def = "{'userSsoId': 1, 'testName': 1}", unique = true),
    @CompoundIndex(name = "userSsoId_idx", def = "{'userSsoId': 1}")
  }
)
@Data
@Document
public class AbTestDecision extends de.is24.common.abtesting.remote.api.AbTestDecision implements DomainObject {
  @Id
  private String id;

  @CreatedBy
  private String createdBy;

  @LastModifiedBy
  private String lastModifiedBy;

  @CreatedDate
  private DateTime created;

  @LastModifiedDate
  private DateTime modified;

  @Override
  public String toString() {
    return "AbTestDecision{" +
      "id='" + id + '\'' +
      '}';
  }
}
