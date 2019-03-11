package com.appdirect.demo.filesource.gcp.domain.exception;

import java.io.IOException;

public final class GcpIOException extends GcpException {

  public GcpIOException(String message) {
    super(message);
  }

  public GcpIOException(String message, IOException cause) {
    super(message, cause);
  }
}
