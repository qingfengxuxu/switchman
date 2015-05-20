package de.is24.common.abtesting.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import de.is24.common.abtesting.remote.api.AbTestConfiguration;
import de.is24.common.abtesting.remote.api.AbTestVariant;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;


public class AbTestConfigurationRestServiceIT extends RestServiceIT {
  public static final String URL_CONFIG = "abTestConfigurations";

  @Test
  public void listsAllAvailableResourcesRequiresAuthentication() {
    ResponseEntity<String> entity = anonymousRestTemplate.exchange(getBaseUrl(),
      HttpMethod.GET,
      requestEntity(),
      String.class);

    assertThat(entity.getStatusCode(), is(equalTo(HttpStatus.UNAUTHORIZED)));

  }

  @Test
  public void listsAllAvailableResourcesWithAuthenticatedUser() throws IOException {
    ResponseEntity<Resource> entity = userRestTemplate.exchange(getBaseUrl(),
      HttpMethod.GET,
      requestEntity(),
      Resource.class);

    assertThat(entity.getStatusCode(), is(equalTo(HttpStatus.OK)));

    List<Link> links = entity.getBody().getLinks();
    List<String> rels = Lists.transform(links, new Function<Link, String>() {
        @Override
        public String apply(@Nullable Link link) {
          return link.getRel();
        }
      });

    assertThat(rels, hasItems("abTestConfigurations", "abTestDecisions", "featureStates"));
  }


  @Test
  public void returnsConfigurationWithAuthenticatedUser() throws IOException {
    createConfiguration(adminRestTemplate, "test");

    ResponseEntity<AbTestConfiguration> entity = userRestTemplate.exchange(getBaseUrl() + URL_CONFIG + "/test",
      HttpMethod.GET,
      requestEntity(),
      AbTestConfiguration.class);

    assertThat(entity.getStatusCode(), is(equalTo(HttpStatus.OK)));
    System.out.println(entity.getBody());
    assertThat(entity.getBody(), notNullValue());
    assertThat(entity.getBody(), hasTestNameProperty());
  }

  private Matcher<AbTestConfiguration> hasTestNameProperty() {
    return new FeatureMatcher<AbTestConfiguration, String>(notNullValue(),
      "abTestConfiguration with testName property",
      "testName property") {
      @Override
      protected String featureValueOf(AbTestConfiguration abTestConfiguration) {
        return abTestConfiguration.getName();
      }
    };
  }

  @Test
  public void returnsAllConfigurationsWithAuthenticatedUser() throws IOException {
    ResponseEntity<Resources<AbTestConfiguration>> entity = userRestTemplate.exchange(getBaseUrl() + URL_CONFIG,
      HttpMethod.GET,
      requestEntity(),
      new ParameterizedTypeReference<Resources<AbTestConfiguration>>() {
      });

    assertThat(entity.getStatusCode(), is(equalTo(HttpStatus.OK)));
  }


  @Test
  public void forbiddenToCreateAbTestConfigurationWithAuthenticatedUser() throws IOException {
    ResponseEntity<String> entity = createConfiguration(userRestTemplate, UUID.randomUUID().toString());

    assertThat(entity.getStatusCode(), is(equalTo(HttpStatus.FORBIDDEN)));
  }

  @Test
  public void createsAbTestConfigurationWithAdminUser() throws IOException {
    ResponseEntity<String> entity = createConfiguration(adminRestTemplate, UUID.randomUUID().toString());

    assertThat(entity.getStatusCode(), is(equalTo(HttpStatus.CREATED)));
  }

  @Test
  public void usersCannotDeleteAbTestConfigurations() throws IOException {
    String uuid = UUID.randomUUID().toString();
    createConfiguration(adminRestTemplate, uuid);

    ResponseEntity<String> entity = deleteConfiguration(userRestTemplate, uuid);

    assertThat(entity.getStatusCode(), is(Matchers.equalTo(HttpStatus.FORBIDDEN)));
  }

  @Test
  public void adminsMayDeleteAbTestConfigurations() throws IOException {
    String uuid = UUID.randomUUID().toString();
    createConfiguration(adminRestTemplate, uuid);

    ResponseEntity<String> entity = deleteConfiguration(adminRestTemplate, uuid);

    assertThat(entity.getStatusCode(), is(Matchers.equalTo(HttpStatus.NO_CONTENT)));
  }


  private ResponseEntity<String> createConfiguration(RestTemplate restTemplate, String name)
                                              throws JsonProcessingException {
    return restTemplate.exchange(getBaseUrl() + URL_CONFIG,
      HttpMethod.POST,
      requestEntity(getAbTestConfiguration(name)),
      String.class);
  }

  private ResponseEntity<String> deleteConfiguration(RestTemplate restTemplate, String name)
                                              throws JsonProcessingException {
    return restTemplate.exchange(getBaseUrl() + URL_CONFIG + "/" + name,
      HttpMethod.DELETE,
      null,
      String.class);
  }

  private AbTestConfiguration getAbTestConfiguration(String name) {
    AbTestConfiguration abTestConfiguration = new AbTestConfiguration();
    abTestConfiguration.setName(name);
    abTestConfiguration.setFrom(new DateTime().minusDays(2));
    abTestConfiguration.setTo(new DateTime().plusDays(2));
    abTestConfiguration.getVariants().add(new AbTestVariant(0, "Variant 1", 40));
    abTestConfiguration.getVariants().add(new AbTestVariant(1, "Variant 2", 60));
    return abTestConfiguration;
  }

}
