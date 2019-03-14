package com.appdirect.demo.filesource.gcp;

import static java.lang.String.format;

import com.appdirect.demo.filesource.gcp.cloudevents.CloudEventsClient;
import com.appdirect.demo.filesource.gcp.domain.bo.SourceEvent;
import com.appdirect.demo.filesource.gcp.domain.bo.UploadNotification;
import com.appdirect.demo.filesource.gcp.domain.exception.GcpException;
import com.appdirect.demo.filesource.gcp.domain.exception.GcpIOException;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.UUID;
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

  @Autowired
  public GcpHandler(ApplicationContext appContext, Storage storage, CloudEventsClient client) {
    this.appContext = appContext;
    this.storage = storage;
    this.client = client;
  }

  @StreamListener(Sink.INPUT)
  public void handleMessage(Message<UploadNotification> message) {

    UploadNotification payload = message.getPayload();
    log.info("Received: {}", payload);

    try {
      Resource gcsFile = appContext
          .getResource(format("gs://%s/%s", payload.getBucketId(), payload.getObjectId()));

      if (gcsFile.exists() && "OBJECT_FINALIZE".equalsIgnoreCase(payload.getEventType())) {

        String content = StreamUtils
            .copyToString(gcsFile.getInputStream(), Charset.defaultCharset());

        Stream.of(content.split("\\r?\\n"))
            .forEach(re -> {
              log.info("Sending Record: {}", re);
              client.publish(SourceEvent.builder()
                  .referenceId(UUID.randomUUID().toString())
                  .eventStr(re)
                  .build());
            });

      } else {
        log.error("Not a valid payload, bucket: {}, object: {}",
            payload.getBucketId(), payload.getObjectId());
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
