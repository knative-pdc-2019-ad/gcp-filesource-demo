package com.appdirect.demo.filesource.gcp.cloudevents;

import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventBuilder;
import java.net.URI;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "cloudevents.source.event")
@Getter
@Setter
public class CloudEventsEvent {

  private String eventType;
  private String source;
  private String contentType;

  public CloudEvent toCloudEvent(Object o) {
    return new CloudEventBuilder()
        .data(o)
        .id(UUID.randomUUID().toString())
        .type(eventType)
        .source(URI.create(source))
        .contentType(contentType)
        .build();
  }
}
