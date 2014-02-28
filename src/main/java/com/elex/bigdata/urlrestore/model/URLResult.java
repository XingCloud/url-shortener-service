package com.elex.bigdata.urlrestore.model;

import org.apache.commons.collections.MapUtils;

import java.util.Map;

/**
 * User: Z J Wu Date: 14-2-27 Time: 下午6:01 Package: com.elex.bigdata.urlrestore.model
 */
public class URLResult {
  private int status;

  private String url;

  public URLResult(Map<String, Object> resultMap) {
    if (MapUtils.isEmpty(resultMap)) {
      this.status = -1;
      return;
    }
    Object obj = resultMap.get("status");
    if (obj == null) {
      this.status = -1;
      return;
    }
    this.status = Integer.parseInt(obj.toString());
    if (this.status != 0) {
      return;
    }
    this.url = resultMap.get("url").toString();
  }

  public int getStatus() {
    return status;
  }

  public String getUrl() {
    return url;
  }

  public boolean isValid() {
    return status == 0;
  }
}
