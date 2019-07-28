package ca.jrvs.apps.trading.dao;

public class ResourceNotFoundException extends RuntimeException {

  public ResourceNotFoundException(String msg) {
    super(msg);
  }

  public ResourceNotFoundException(String msg, Exception ex) {
    super(msg, ex);
  }
}
