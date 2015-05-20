package de.is24.common.infrastructure.domain;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;


@AllArgsConstructor
@Data
@NoArgsConstructor
public class Client implements UserDetails, DomainObject {
  @Id
  private String name;

  private String accessKey;
  private List<String> roles;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Lists.transform(roles, new Function<String, GrantedAuthority>() {
        @Override
        public GrantedAuthority apply(@Nullable String role) {
          return new SimpleGrantedAuthority("ROLE_" + role);
        }
      });
  }

  @Override
  public String getPassword() {
    return accessKey;
  }

  @Override
  public String getUsername() {
    return name;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public String getId() {
    return name;
  }
}
