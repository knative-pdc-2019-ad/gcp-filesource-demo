package com.appdirect.demo.filesource.gcp.cloudevents;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "cloudevents.source.client")
@Getter
@Setter
public class ClientProperties {

  private String targetUri;
  private int connectTimeout;
  private int readTimeout;
}
