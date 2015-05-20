package de.is24.common.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import de.is24.common.infrastructure.config.serialization.ReportingDateTimeSerializationModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;


@Configuration
public class Reporting {
  @Bean
  public RestOperations hadoopRestOperations() {
    RestTemplate restTemplate = new RestTemplate();
    MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new ReportingDateTimeSerializationModule());
    mappingJackson2HttpMessageConverter.setObjectMapper(objectMapper);
    restTemplate.setMessageConverters(Lists.<HttpMessageConverter<?>>newArrayList(mappingJackson2HttpMessageConverter));
    return restTemplate;
  }
}
