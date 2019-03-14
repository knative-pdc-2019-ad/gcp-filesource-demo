package com.appdirect.demo.filesource.gcp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Sink;


@SpringBootApplication
@EnableBinding(Sink.class)
public class GcpFileSource {

  public static void main(String[] args) {
    SpringApplication.run(GcpFileSource.class, args);
  }
}
