package com.appdirect.demo.filesource.gcp.domain.bo;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Builder
@Data
public class SourceEvent {

  @NonNull
  private String referenceId;

  @NonNull
  private String eventStr;
}
