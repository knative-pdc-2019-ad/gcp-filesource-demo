package com.appdirect.demo.filesource.gcp.domain.bo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UploadNotification {

  private String eventType;
  private String payloadFormat;
  private String bucketId;
  private String objectId;
  private String objectGeneration;
}
