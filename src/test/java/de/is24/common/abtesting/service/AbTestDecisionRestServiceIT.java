package de.is24.common.abtesting.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Lists;
import de.is24.common.abtesting.remote.api.AbTestDecision;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.core.AllOf;
import org.junit.After;
import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.hamcrest.core.Is.is;


public class AbTestDecisionRestServiceIT extends RestServiceIT {
  public static final String URL_CONFIG = "abTestDecisions";
  public static final String FIRST_USER_SSO_ID = "1234";
  public static final Integer FIRST_USER_VARIANT = 0;
  public static final String SECOND_USER_SSO_ID = "2345";
  public static final Integer SECOND_USER_VARIANT = 1;
  public static final String TEST_AB_TEST = "TestABTest";
  private static final String DELETE_LINK = "/search/deleteByTestName";
  private ResponseEntity<?> response;

  private List<AbTestDecision> createdDecisions = Lists.newArrayList();

  @Test
  public void usersMayReadAbTestDecisions() throws IOException {
    givenExistingTestDecisions();
    whenUserReadsTestDecisions();
    thenResponseIs(HttpStatus.OK);
    thenAllCreatedTestDecisionsCanBeDeserialized();
  }

  @Test
  public void usersMayCreateDecisions() throws IOException {
    whenUserCreatesTestDecision();
    thenResponseIs(HttpStatus.CREATED);
  }

  @Test
  public void usersMayNotDeleteDecisions() throws IOException {
    whenUserDeletesDecision("123");
    thenResponseIs(HttpStatus.FORBIDDEN);
  }

  @Test
  public void usersMayNotDeleteAllDecisionsForATest() throws IOException {
    whenUserDeletesAllDecisionsOfATest();
    thenResponseIs(HttpStatus.FORBIDDEN);
  }

  @Test
  public void adminsMayDeleteAllDecisionsForATest() throws IOException {
    givenExistingTestDecisions();
    whenAdminDeletesAllDecisionsOfATest();
    thenResponseIs(HttpStatus.OK);
  }

  private void whenUserCreatesTestDecision() throws IOException {
    response = createDecision(userRestTemplate, TEST_AB_TEST, FIRST_USER_SSO_ID, FIRST_USER_VARIANT);
  }

  private void whenUserDeletesAllDecisionsOfATest() {
    response = userRestTemplate.exchange(
      getBaseUrl() + URL_CONFIG + DELETE_LINK + "?testName={testName}",
      HttpMethod.GET,
      requestEntity(),
      (Class) null,
      TEST_AB_TEST);
  }

  private void whenAdminDeletesAllDecisionsOfATest() {
    response = adminRestTemplate.exchange(
      getBaseUrl() + URL_CONFIG + DELETE_LINK + "?testName={testName}",
      HttpMethod.GET,
      requestEntity(),
      (Class) null,
      TEST_AB_TEST);
  }

  @After
  public void deleteCreatedDecisions() {
    for (AbTestDecision decision : createdDecisions) {
      ResponseEntity<Resources<Resource<AbTestDecision>>> response = userRestTemplate.exchange(
        getBaseUrl() + URL_CONFIG + "/search/findByTestNameAndUserSsoId?testName={testName}&userSsoId={userSsoId}",
        HttpMethod.GET,
        requestEntity(),
        new ParameterizedTypeReference<Resources<Resource<AbTestDecision>>>() {
        },
        decision.getTestName(),
        decision.getUserSsoId());
      Iterator<Resource<AbTestDecision>> receivedResponses = response.getBody().getContent().iterator();
      if (receivedResponses.hasNext()) {
        adminRestTemplate.delete(receivedResponses.next().getLink(Link.REL_SELF).getHref());
      }
    }
  }

  @SuppressWarnings("unchecked")
  private void thenAllCreatedTestDecisionsCanBeDeserialized() {
    Resources<AbTestDecision> body = (Resources<AbTestDecision>) response.getBody();
    Collection<AbTestDecision> respondedDecisions = body.getContent();
    assertThat(respondedDecisions,
      contains(
        abTestDecisionWithSsoIdAndVariant(FIRST_USER_SSO_ID, FIRST_USER_VARIANT),
        abTestDecisionWithSsoIdAndVariant(SECOND_USER_SSO_ID, SECOND_USER_VARIANT)));
  }

  private Matcher<AbTestDecision> abTestDecisionWithSsoIdAndVariant(final String firstUserSsoId,
                                                                    final int firstUserVariant) {
    return AllOf.allOf(
      new FeatureMatcher<AbTestDecision, String>(equalTo(firstUserSsoId), "AbTestDecision for SSO-ID", "SSO-ID") {
        @Override
        protected String featureValueOf(AbTestDecision abTestDecision) {
          return abTestDecision.getUserSsoId();
        }
      },
      new FeatureMatcher<AbTestDecision, Integer>(equalTo(firstUserVariant), "AbTestDecision with variant", "variant") {
        @Override
        protected Integer featureValueOf(AbTestDecision abTestDecision) {
          return abTestDecision.getVariantId();
        }
      });
  }

  private void givenExistingTestDecisions() throws IOException {
    createDecision(adminRestTemplate, TEST_AB_TEST, FIRST_USER_SSO_ID, FIRST_USER_VARIANT);
    createDecision(adminRestTemplate, TEST_AB_TEST, SECOND_USER_SSO_ID, SECOND_USER_VARIANT);
  }

  private void whenUserReadsTestDecisions() {
    response = userRestTemplate.exchange(getBaseUrl() + URL_CONFIG,
      HttpMethod.GET,
      requestEntity(),
      new ParameterizedTypeReference<Resources<AbTestDecision>>() {
      });
  }

  private void whenUserDeletesDecision(String idOfDecisionToRemove) {
    String url = getBaseUrl() + URL_CONFIG + "/" + idOfDecisionToRemove;
    response = userRestTemplate.exchange(url, HttpMethod.DELETE, null, String.class);
  }

  private void thenResponseIs(HttpStatus status) {
    assertThat(response.getStatusCode(), is(status));
  }

  private ResponseEntity<String> createDecision(RestTemplate restTemplate, String testName, String userSsoId,
                                                int variantId) throws JsonProcessingException {
    AbTestDecision abTestDecision = createAbTestDecision(testName, userSsoId, variantId);
    ResponseEntity<String> responseEntity = restTemplate.exchange(getBaseUrl() + URL_CONFIG,
      HttpMethod.POST,
      requestEntity(abTestDecision),
      String.class);
    createdDecisions.add(abTestDecision);
    return responseEntity;
  }

  private AbTestDecision createAbTestDecision(String testName, String userSsoId, int variantId) {
    AbTestDecision abTestDecision = new AbTestDecision();
    abTestDecision.setTestName(testName);
    abTestDecision.setUserSsoId(userSsoId);
    abTestDecision.setVariantId(variantId);
    return abTestDecision;
  }


}
