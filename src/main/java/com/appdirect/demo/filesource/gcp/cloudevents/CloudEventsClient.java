package com.appdirect.demo.filesource.gcp.cloudevents;

import io.cloudevents.CloudEvent;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class CloudEventsClient {

  private final RestTemplate restClient;
  private final String targetUri;
  private CloudEventsEvent cloudEventsEvent;

  public CloudEventsClient(RestTemplate restClient, CloudEventsEvent cloudEventsEvent,
      String targetUri) {
    this.restClient = restClient;
    this.targetUri = targetUri;
    this.cloudEventsEvent = cloudEventsEvent;
  }

  public ResponseEntity publish(Object data) {
    CloudEvent event = cloudEventsEvent.toCloudEvent(data);
    return this.restClient.postForEntity(this.targetUri, event, Object.class);

//          if (httpStatus.is4xxClientError() || httpStatus.is5xxServerError()) {
//            throw WebClientResponseException.create(
//                httpStatus.value(),
//                httpStatus.getReasonPhrase(),
//                response.headers().asHttpHeaders(),
//                null,
//                null);
//          }
  }
}
