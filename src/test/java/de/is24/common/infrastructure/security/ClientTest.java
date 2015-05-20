package de.is24.common.infrastructure.security;

import com.google.common.collect.Lists;
import de.is24.common.infrastructure.domain.Client;
import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.IsEqual.equalTo;


public class ClientTest {
  @Test
  public void transformsRolesToGrantedAuthories() {
    Client client = getClient();

    assertThat(client.getAuthorities(), hasSize(2));
    assertThat(client.getAuthorities(),
      containsInAnyOrder((GrantedAuthority) new SimpleGrantedAuthority("ROLE_USER"),
        (GrantedAuthority) new SimpleGrantedAuthority("ROLE_ADMIN")));
  }

  @Test
  public void returnsAccessKeyAndNameAsUserDetails() {
    Client client = getClient();
    assertThat(client.getUsername(), equalTo(client.getName()));
    assertThat(client.getPassword(), equalTo(client.getAccessKey()));
  }

  private Client getClient() {
    Client client = new Client();
    client.setAccessKey("password");
    client.setName("name");

    client.setRoles(Lists.newArrayList("USER", "ADMIN"));
    return client;
  }
}
