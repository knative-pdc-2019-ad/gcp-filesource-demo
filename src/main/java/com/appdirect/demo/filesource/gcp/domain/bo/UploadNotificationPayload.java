package com.appdirect.demo.filesource.gcp.domain.bo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UploadNotificationPayload {

  private String bucket;
  private String name;
  private String eventType;
}
