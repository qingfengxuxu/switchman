package de.is24.common.abtesting.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.is24.common.infrastructure.config.Application;
import de.is24.common.infrastructure.config.serialization.JsonMarshallingConfigModule;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.hateoas.hal.Jackson2HalModule;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;
import java.util.Arrays;


@ActiveProfiles("test")
@IntegrationTest("server.port=0")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public abstract class RestServiceIT {
  @Value("${local.server.port}")
  protected int port;

  @Autowired
  protected ObjectMapper objectMapper;

  protected RestTemplate userRestTemplate;
  protected RestTemplate adminRestTemplate;
  protected RestTemplate anonymousRestTemplate;

  @Before
  public void setUp() {
    userRestTemplate = new TestRestTemplate("user", "pass");
    adminRestTemplate = new TestRestTemplate("admin", "admin");
    anonymousRestTemplate = new TestRestTemplate();

    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    objectMapper.registerModule(new Jackson2HalModule());
    objectMapper.registerModule(new JsonMarshallingConfigModule());

    updateMessageConverter(userRestTemplate);
    updateMessageConverter(adminRestTemplate);
    updateMessageConverter(anonymousRestTemplate);

  }

  private void updateMessageConverter(RestTemplate restTemplate) {
    for (HttpMessageConverter messageConverter : restTemplate.getMessageConverters()) {
      if (messageConverter instanceof MappingJackson2HttpMessageConverter) {
        MappingJackson2HttpMessageConverter jsonMessageConverter = (MappingJackson2HttpMessageConverter)
          messageConverter;
        jsonMessageConverter.setSupportedMediaTypes(MediaType.parseMediaTypes("application/hal+json,application/json"));
        jsonMessageConverter.setObjectMapper(objectMapper);
      }
    }
  }

  protected HttpHeaders getHeaders() {
    HttpHeaders headers = new HttpHeaders();
    MediaType mediaType = new MediaType("application", "hal+json");
    headers.setContentType(mediaType);
    headers.setAccept(Arrays.asList(mediaType));
    return headers;
  }

  protected String getBaseUrl() {
    return new StringBuilder("http://localhost:").append(port).append("/api/").toString();
  }

  protected HttpEntity<Object> requestEntity() {
    return new HttpEntity<Object>(getHeaders());
  }

  protected HttpEntity<Object> requestEntity(Object object) throws JsonProcessingException {
    String json = objectMapper.writeValueAsString(object);
    return new HttpEntity<Object>(json, getHeaders());
  }
}
