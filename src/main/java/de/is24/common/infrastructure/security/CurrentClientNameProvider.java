package de.is24.common.infrastructure.security;

import de.is24.common.infrastructure.domain.Client;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


public class CurrentClientNameProvider {
  public static String getCurrentClientName() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null) {
      Client client = (Client) authentication.getPrincipal();
      return client.getName();
    }
    throw new IllegalStateException("Unknown client tried to change data in the repository which should be secured.");
  }
}
