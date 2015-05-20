package de.is24.common.infrastructure.config.security;

import com.google.common.collect.Lists;
import de.is24.common.infrastructure.domain.Client;
import de.is24.common.infrastructure.repo.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;


@Configuration
@EnableConfigurationProperties(PredefinedUsers.class)
@EnableWebMvcSecurity
public class Security extends WebSecurityConfigurerAdapter {
  private static final String ADMIN_ROLE = "ADMIN";

  @Autowired
  private PredefinedUsers predefinedUsers;

  @Autowired
  private UserDetailsService userDetailsService;

  @Autowired
  private ClientRepository clientRepository;


  @Autowired
  protected void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService);
  }

  @Configuration
  @Order(1)
  public static class ApiWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
      http.csrf()
      .disable()
      .antMatcher("/api/**")
      .authorizeRequests()
      .antMatchers(HttpMethod.POST, "/api/abTestConfigurations")
      .hasRole(ADMIN_ROLE)
      .antMatchers(HttpMethod.PUT, "/api/abTestConfigurations/**")
      .hasRole(ADMIN_ROLE)
      .antMatchers(HttpMethod.GET, "/api/abTestDecisions/search/deleteByTestName*")
      .hasRole(ADMIN_ROLE)
      .antMatchers(HttpMethod.DELETE, "/api/abTestConfigurations/**", "/api/abTestDecisions/**")
      .hasRole(ADMIN_ROLE)
      .antMatchers("/api/**")
      .hasRole("USER")
      .and()
      .httpBasic()
      .and()
      .sessionManagement()
      .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }
  }

  @Configuration
  @Order(2)
  public static class FormLoginWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
      http.csrf().disable();
      http.authorizeRequests()
      .antMatchers("/admin/**", "/hal-browser/**")
      .hasRole("ADMIN")
      .and()
      .formLogin()
      .loginPage("/login");
    }
  }

  @PostConstruct
  public void addTestUser() {
    savePredefinedUsers(predefinedUsers.getUsers(), Lists.newArrayList("USER"));
    savePredefinedUsers(predefinedUsers.getAdmins(), Lists.newArrayList("USER", "ADMIN"));
  }

  private void savePredefinedUsers(List<PredefinedUser> users, ArrayList<String> roles) {
    if (users != null) {
      for (PredefinedUser user : users) {
        savePredefinedUser(user.getName(), user.getPassword(), roles);
      }
    }
  }

  private void savePredefinedUser(String username, String pass, ArrayList<String> roles) {
    Client user = new Client();
    user.setName(username);
    user.setAccessKey(pass);
    user.setRoles(roles);
    clientRepository.save(user);
  }
}
