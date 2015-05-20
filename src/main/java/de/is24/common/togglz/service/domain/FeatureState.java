package de.is24.common.togglz.service.domain;

import de.is24.common.infrastructure.domain.DomainObject;
import de.is24.common.togglz.remote.api.RemoteFeatureState;
import lombok.Data;
import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@Document
public class FeatureState extends RemoteFeatureState implements DomainObject {
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
    return "FeatureState{" +
      "id='" + id + '\'' +
      '}';
  }
}
