package com.elex.bigdata.urlrestore.exception;

/**
 * User: Z J Wu Date: 14-2-27 Time: 下午5:57 Package: com.elex.bigdata.urlrestore.exception
 */
public class URLRestoreException extends Exception {
  public URLRestoreException() {
  }

  public URLRestoreException(String message) {
    super(message);
  }

  public URLRestoreException(String message, Throwable cause) {
    super(message, cause);
  }

  public URLRestoreException(Throwable cause) {
    super(cause);
  }
}
