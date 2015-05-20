package de.is24.common.infrastructure.repo;

import de.is24.common.infrastructure.domain.DomainObject;
import de.is24.common.infrastructure.security.CurrentClientNameProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.rest.core.event.AbstractRepositoryEventListener;
import org.springframework.stereotype.Component;


@Component
public class RepositoryChangedLogger extends AbstractRepositoryEventListener<DomainObject> {
  private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryChangedLogger.class);

  @Override
  protected void onAfterCreate(DomainObject entity) {
    super.onAfterSave(entity);
    logChange(entity, "CREATED");
  }

  @Override
  protected void onAfterSave(DomainObject entity) {
    super.onAfterSave(entity);
    logChange(entity, "SAVED");
  }

  @Override
  protected void onAfterDelete(DomainObject entity) {
    super.onAfterDelete(entity);
    logChange(entity, "DELETED");
  }

  private void logChange(DomainObject entity, final String event) {
    LOGGER.info("{} entity {} #{}; Responsible client: {}",
      event,
      entity.getClass().getSimpleName(),
      entity.getId(),
      CurrentClientNameProvider.getCurrentClientName());
  }


}
