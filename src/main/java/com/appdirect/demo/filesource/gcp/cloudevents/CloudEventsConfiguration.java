package com.appdirect.demo.filesource.gcp.cloudevents;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;


@Configuration
public class CloudEventsConfiguration {

  public final static String MEDIA_TYPE_CLOUD_EVENTS = "application/cloudevents+json";

  private ClientProperties clientProperties;
  private CloudEventsEvent cloudEventsEvent;

  @Autowired
  public CloudEventsConfiguration(ClientProperties clientProperties,
      CloudEventsEvent cloudEventsEvent) {
    this.clientProperties = clientProperties;
    this.cloudEventsEvent = cloudEventsEvent;
  }

  @Bean
  @ConditionalOnMissingBean
  public CloudEventsClient cloudEventPublisher(RestTemplate restTemplate) {
    return new CloudEventsClient(restTemplate, cloudEventsEvent, clientProperties.getTargetUri());
  }

  @Bean
  public RestTemplate restClient() {
    HttpComponentsClientHttpRequestFactory httpRequestFactory
        = new HttpComponentsClientHttpRequestFactory();

    httpRequestFactory.setConnectionRequestTimeout(5);
    httpRequestFactory.setConnectTimeout(clientProperties.getConnectTimeout());
    httpRequestFactory.setReadTimeout(clientProperties.getReadTimeout());

    RestTemplate rt = new RestTemplate(httpRequestFactory);
    rt.getInterceptors().add(new HeaderRequestInterceptor());
    return rt;
  }

  private class HeaderRequestInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
        ClientHttpRequestExecution execution) throws IOException {
      request.getHeaders().setContentType(new MediaType(MEDIA_TYPE_CLOUD_EVENTS));
      return execution.execute(request, body);
    }
  }
}
