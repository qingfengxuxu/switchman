package de.is24.common.abtesting.service.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.is24.common.infrastructure.domain.DomainObject;
import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;


@Document
public class AbTestConfiguration extends de.is24.common.abtesting.remote.api.AbTestConfiguration
  implements DomainObject {
  @CreatedDate
  private DateTime created;

  @LastModifiedDate
  private DateTime modified;

  @CreatedBy
  private String createdBy;

  @LastModifiedBy
  private String lastModifiedBy;

  @JsonIgnore
  @Override
  @Transient
  public String getId() {
    return getName();
  }

  @Id
  @Override
  public String getName() {
    return super.getName();
  }

  @Transient
  public boolean isActive() {
    DateTime now = DateTime.now();
    return (getFrom() != null) && (getTo() != null) && getFrom().isBefore(now) && getTo().isAfter(now);
  }

  public DateTime getCreated() {
    return created;
  }

  public DateTime getModified() {
    return modified;
  }
}
