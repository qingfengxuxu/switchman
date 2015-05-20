package de.is24.common.infrastructure.repo;

import de.is24.common.infrastructure.domain.Client;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;


@RepositoryRestResource(exported = false)
public interface ClientRepository extends PagingAndSortingRepository<Client, String> {
  Client findByName(String name);
}
