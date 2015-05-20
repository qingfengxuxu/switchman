package de.is24.common.infrastructure.security;

import de.is24.common.infrastructure.domain.Client;
import de.is24.common.infrastructure.repo.ClientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {
  private static final Logger LOGGER = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

  private ClientRepository clientRepository;


  @Autowired
  public UserDetailsServiceImpl(ClientRepository clientRepository) {
    this.clientRepository = clientRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Client client = clientRepository.findByName(username);
    if (client == null) {
      LOGGER.error("User name {} could not be retrieved.", username);
      throw new UsernameNotFoundException("not found.");
    }
    return client;
  }
}
