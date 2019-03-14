package com.appdirect.demo.filesource.gcp;

import static org.slf4j.LoggerFactory.getLogger;

import com.appdirect.demo.filesource.gcp.cloudevents.ClientProperties;
import com.appdirect.demo.filesource.gcp.cloudevents.CloudEventsClient;
import com.appdirect.demo.filesource.gcp.cloudevents.CloudEventsEvent;
import java.lang.invoke.MethodHandles;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

//@SpringBootApplication
//@EnableBinding(Sink.class)
//public class GcpFileSource {
//
//  public static void main(String[] args) {
//    SpringApplication.run(GcpFileSource.class, args);
//  }
//}

@SpringBootApplication
@EnableConfigurationProperties({ClientProperties.class, CloudEventsEvent.class})
public class GcpFileSource { //implements CommandLineRunner {

  private static final Logger LOGGER = getLogger(MethodHandles.lookup().lookupClass());

  private CloudEventsClient client;

  @Autowired
  public GcpFileSource(CloudEventsClient client) {
    this.client = client;
  }

//  @Override
//  public void run(String... args) {
//    while (true) {
//      LOGGER.error("Sending RECORDS");
//      Stream
//          .of(
//              "1,1552232089,user1,product1,10,1.75",
//              "2,1552231089,user2,product1,12,1.75",
//              "3,1552232089,user3,product2,8,2.25",
//              "4,1552232089,user4,product2,9,2.25"
//          )
//          .forEach(re -> {
//            LOGGER.error("Record: {}", re);
//            client.publish(SourceEvent.builder()
//                .referenceId(UUID.randomUUID().toString())
//                .eventStr(re)
//                .build());
//          });
//      LOGGER.error("Sent RECORDS");
//    }
//  }

  public static void main(String[] args) {
    SpringApplication.run(GcpFileSource.class, args);
  }
}