package de.is24.common.infrastructure.security;

import com.google.common.collect.Lists;
import de.is24.common.infrastructure.domain.Client;
import de.is24.common.infrastructure.repo.ClientRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class UserDetailsServiceImplTest {
  public static final String NAME = "user";
  public static final String UNKNOWN = "unknown";
  @Mock
  ClientRepository clientRepositoryMock;
  private Client client = new Client(NAME, "pass", Lists.newArrayList("USER", "ADMIN"));
  private UserDetailsServiceImpl userDetailsService;


  @Before
  public void setUp() {
    userDetailsService = new UserDetailsServiceImpl(clientRepositoryMock);
    when(clientRepositoryMock.findByName(NAME)).thenReturn(client);
  }

  @Test
  public void testLoadUserByUsername() throws Exception {
    assertThat(userDetailsService.loadUserByUsername(NAME), equalTo((UserDetails) client));
    verify(clientRepositoryMock, times(1)).findByName(anyString());
  }


  @Test(expected = UsernameNotFoundException.class)
  public void throwsExceptionWhenUserNotFound() throws Exception {
    userDetailsService.loadUserByUsername(UNKNOWN);
    verify(clientRepositoryMock, times(1)).findByName(anyString());
  }
}
