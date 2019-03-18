package com.appdirect.demo.filesource.gcp;

import static java.lang.Runtime.getRuntime;
import static java.util.concurrent.Executors.newFixedThreadPool;

import com.appdirect.demo.filesource.gcp.cloudevents.CloudEventsClient;
import com.appdirect.demo.filesource.gcp.domain.bo.SourceEvent;
import com.appdirect.demo.filesource.gcp.domain.bo.UploadNotificationPayload;
import com.appdirect.demo.filesource.gcp.domain.exception.GcpException;
import com.appdirect.demo.filesource.gcp.domain.exception.GcpIOException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

@Service
@Slf4j
public class GcpHandler {

  private Storage storage;
  private CloudEventsClient client;
  private ApplicationContext appContext;
  private final ObjectMapper mapper;
  private final ExecutorService executors;

  @Autowired
  public GcpHandler(ApplicationContext appContext, Storage storage, CloudEventsClient client) {
    this.appContext = appContext;
    this.storage = storage;
    this.client = client;

    this.executors = newFixedThreadPool(getRuntime().availableProcessors() * 4);
    this.mapper = new ObjectMapper();
    this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  @StreamListener(Sink.INPUT)
  public void handleMessage(Message<String> message) {

    log.info("Payload: {}", message.getPayload());

    try {
      UploadNotificationPayload payload = this.mapper.readValue(message.getPayload(),
          UploadNotificationPayload.class);

      Resource gcsFile = appContext
          .getResource(String.format("gs://%s/%s", payload.getBucket(), payload.getName()));

      if (gcsFile.exists() && (payload.getEventType() == null ||
          "OBJECT_FINALIZE".equalsIgnoreCase(payload.getEventType()))) {

        String content = StreamUtils
            .copyToString(gcsFile.getInputStream(), Charset.defaultCharset());

        Stream.of(content.split("\\r?\\n"))
            .forEach(re -> executors.submit(() -> {
              log.info("Sending Record: {}", re);
              client.publish(SourceEvent.builder()
                  .referenceId(UUID.randomUUID().toString())
                  .eventStr(re)
                  .build());
            }));
      } else {
        log.error("Not a valid payload, bucket: {}, object: {}",
            payload.getBucket(), payload.getName());
      }

      //    try {
      //      Blob gcsFile = gcsFile(payload.getBucketId(), payload.getObjectId());
      //      Path dest = downloadLocal(gcsFile);
      //      Files.lines(dest)
      //          .forEach(re ->
      //              client.publish(SourceEvent.builder()
      //                  .referenceId(UUID.randomUUID().toString())
      //                  .eventStr(re)
      //                  .build())
      //          );
      //      removeRemote(gcsFile);

    } catch (StorageException e) {
      throw new GcpException("Storage API error", e);
    } catch (IOException e) {
      throw new GcpIOException("IO error in parsing", e);
    }
  }

//  private Path downloadLocal(Blob gcsFile) throws StorageException {
//    Path dest = Paths.get(System.getProperty("java.io.tmpdir"), gcsFile.getBlobId().getName());
//    gcsFile.downloadTo(dest);
//    return dest;
//  }
//
//  private void removeRemote(Blob gcsFile) throws StorageException {
//    boolean deleted = gcsFile.delete();
//    if (!deleted) {
//      log.error("Unable to delete remote file in google storage");
//    }
//  }
//
//  private Blob gcsFile(String bucketId, String objectId) throws StorageException {
//    return storage.get(BlobId.of(bucketId, objectId));
//  }
}
