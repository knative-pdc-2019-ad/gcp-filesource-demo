package com.appdirect.demo.filesource.gcp;

import com.appdirect.demo.filesource.gcp.cloudevents.CloudEventsClient;
import com.appdirect.demo.filesource.gcp.domain.bo.UploadNotification;
import com.appdirect.demo.filesource.gcp.domain.exception.GcpException;
import com.appdirect.demo.filesource.gcp.domain.exception.GcpIOException;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GcpHandler {

  private Storage storage;
  private CloudEventsClient client;

  @Autowired
  public GcpHandler(Storage storage, CloudEventsClient client) {
    this.storage = storage;
    this.client = client;
  }


  @StreamListener(Sink.INPUT)
  public void handleMessage(Message<UploadNotification> message) {

    UploadNotification payload = message.getPayload();
    log.info("Received: {}", payload);

    try {
      Blob gcsFile = gcsFile(payload.getBucketId(), payload.getObjectId());
      Path dest = downloadLocal(gcsFile);
      removeRemote(gcsFile);

//      Path dest = Paths.get("/tmp", "sample.csv");

      Files.lines(dest).forEach(re -> client.publish(re));

    } catch (StorageException e) {
      throw new GcpException("Storage API error", e);
    } catch (IOException e) {
      throw new GcpIOException("IO error in parsing", e);
    }
  }

  private Path downloadLocal(Blob gcsFile) throws StorageException {
    Path dest = Paths.get(System.getProperty("java.io.tmpdir"), gcsFile.getBlobId().getName());
    gcsFile.downloadTo(dest);
    return dest;
  }

  private void removeRemote(Blob gcsFile) throws StorageException {
    boolean deleted = gcsFile.delete();
    if (!deleted) {
      log.error("Unable to delete remote file in google storage");
    }
  }

  private Blob gcsFile(String bucketId, String objectId) throws StorageException {
    return storage.get(BlobId.of(bucketId, objectId));
  }
}
