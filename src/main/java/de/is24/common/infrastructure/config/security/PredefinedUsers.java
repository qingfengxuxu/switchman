package de.is24.common.infrastructure.config.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import java.util.List;


@ConfigurationProperties(locations = { "classpath:predefined-users.yaml" })
public class PredefinedUsers {
  private List<PredefinedUser> users;
  private List<PredefinedUser> admins;


  public List<PredefinedUser> getUsers() {
    return users;
  }

  public List<PredefinedUser> getAdmins() {
    return admins;
  }

  public void setUsers(List<PredefinedUser> users) {
    this.users = users;
  }

  public void setAdmins(List<PredefinedUser> admins) {
    this.admins = admins;
  }
}
