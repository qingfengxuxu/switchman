package de.is24.common.togglz.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.is24.common.abtesting.service.RestServiceIT;
import de.is24.common.togglz.remote.api.RemoteFeature;
import de.is24.common.togglz.service.domain.FeatureState;
import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import java.io.IOException;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;


public class FeatureStateRestServiceIT extends RestServiceIT {
  public static final String URL_CONFIG = "featureStates";

  @Test
  public void listsAllAvailableResourcesRequiresAuthentication() {
    ResponseEntity<String> entity = anonymousRestTemplate.exchange(getBaseUrl(),
      HttpMethod.GET,
      requestEntity(),
      String.class);

    assertThat(entity.getStatusCode(), is(equalTo(HttpStatus.UNAUTHORIZED)));
  }

  @Test
  public void returnsFeatureStateWithAuthenticatedUser() throws IOException {
    createFeatureState(adminRestTemplate, "test");

    ResponseEntity<Resources<FeatureState>> entity = userRestTemplate.exchange(
      getBaseUrl() + URL_CONFIG + "/search/findByFeatureName?name={name}",
      HttpMethod.GET,
      requestEntity(),
      new ParameterizedTypeReference<Resources<FeatureState>>() {
      },
      "test");

    Resources<FeatureState> resources = entity.getBody();

    assertThat(entity.getStatusCode(), is(equalTo(HttpStatus.OK)));
    assertThat(resources.iterator().hasNext(), is(true));

    FeatureState featureState = resources.iterator().next();
    assertThat(featureState.getFeature().getName(), is("test"));
    assertThat(featureState.getEnabled(), is(true));
  }

  @Test
  public void returnsNoFeatureStateWhenSearchingForNonExistentFeatureName() throws IOException {
    createFeatureState(adminRestTemplate, "existent");

    ResponseEntity<Resources<FeatureState>> entity = userRestTemplate.exchange(
      getBaseUrl() + URL_CONFIG + "/search/findByFeatureName?name={name}",
      HttpMethod.GET,
      requestEntity(),
      new ParameterizedTypeReference<Resources<FeatureState>>() {
      },
      "not-existing");

    assertThat(entity.getStatusCode(), is(equalTo(HttpStatus.OK)));

    Resources<FeatureState> resources = entity.getBody();
    assertThat(resources.iterator().hasNext(), is(false));
  }

  private ResponseEntity<String> createFeatureState(RestTemplate restTemplate, String name)
                                             throws JsonProcessingException {
    return restTemplate.exchange(getBaseUrl() + URL_CONFIG,
      HttpMethod.POST,
      requestEntity(getFeatureState(name)),
      String.class);
  }

  private FeatureState getFeatureState(String name) {
    FeatureState featureState = new FeatureState();
    RemoteFeature remoteFeature = new RemoteFeature();
    remoteFeature.setName(name);
    featureState.setFeature(remoteFeature);
    featureState.setEnabled(true);
    return featureState;
  }

}
