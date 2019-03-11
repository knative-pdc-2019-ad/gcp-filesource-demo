package com.appdirect.demo.filesource.gcp.domain.exception;

public class GcpException extends RuntimeException {

  public GcpException(String message) {
    super(message);
  }

  public GcpException(String message, Throwable cause) {
    super(message, cause);
  }
}
