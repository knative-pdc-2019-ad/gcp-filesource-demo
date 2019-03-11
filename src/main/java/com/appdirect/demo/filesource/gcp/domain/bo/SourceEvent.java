package com.appdirect.demo.filesource.gcp.domain.bo;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Builder
@Data
public class SourceEvent {

  @NonNull
  private String referenceId;
  private Long processingTimeMillis;

  @NonNull
  private String eventStr;

  @NonNull
  private String configId;
}
