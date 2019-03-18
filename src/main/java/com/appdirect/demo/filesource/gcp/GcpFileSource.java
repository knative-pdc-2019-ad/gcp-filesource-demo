package com.appdirect.demo.filesource.gcp;

import com.appdirect.demo.filesource.gcp.cloudevents.ClientProperties;
import com.appdirect.demo.filesource.gcp.cloudevents.CloudEventsEvent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Sink;


@SpringBootApplication
@EnableBinding(Sink.class)
@EnableConfigurationProperties({ClientProperties.class, CloudEventsEvent.class})
public class GcpFileSource {

  public static void main(String[] args) {
    SpringApplication.run(GcpFileSource.class, args);
  }
}
