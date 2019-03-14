package com.appdirect.demo.filesource.gcp.cloudevents;

import io.cloudevents.CloudEvent;
import java.io.IOException;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class CloudEventsClient {

//  public final static String MEDIA_TYPE_CLOUD_EVENTS = "application/cloudevents+json";

  private final RestTemplate restClient;

  private ClientProperties clientProperties;
  private CloudEventsEvent cloudEventsEvent;

  @Autowired
  public CloudEventsClient(ClientProperties clientProperties, CloudEventsEvent cloudEventsEvent) {
    this.cloudEventsEvent = cloudEventsEvent;
    this.clientProperties = clientProperties;
    this.restClient = restClient();
  }

  private RestTemplate restClient() {
    HttpComponentsClientHttpRequestFactory httpRequestFactory
        = new HttpComponentsClientHttpRequestFactory();

    httpRequestFactory.setConnectionRequestTimeout(1000);
    httpRequestFactory.setConnectTimeout(clientProperties.getConnectTimeout());
    httpRequestFactory.setReadTimeout(clientProperties.getReadTimeout());

    RestTemplate rt = new RestTemplate(httpRequestFactory);
    rt.getInterceptors().add(new HeaderRequestInterceptor());
    return rt;
  }

  public ResponseEntity publish(Object data) {
    CloudEvent event = cloudEventsEvent.toCloudEvent(data);
    try {
      return this.restClient.postForEntity(clientProperties.getTargetUri(), event, String.class);

    } catch (RestClientException e) {
      log.error("Rest Exception", e);
    }
    return null;
  }

  private class HeaderRequestInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
        ClientHttpRequestExecution execution) throws IOException {

      request.getHeaders()
          .setAccept(Arrays.asList(MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN));
      return execution.execute(request, body);
    }
  }
}
