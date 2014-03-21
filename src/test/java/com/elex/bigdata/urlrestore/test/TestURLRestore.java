package com.elex.bigdata.urlrestore.test;

import com.elex.bigdata.ro.RedisOperationException;
import com.elex.bigdata.urlrestore.exception.URLRestoreException;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * User: Z J Wu Date: 14-2-27 Time: 下午6:12 Package: com.elex.bigdata.urlrestore.test
 */
public class TestURLRestore {

  @Test
  public void test() throws RedisOperationException, IOException, URLRestoreException, URISyntaxException {
    URIBuilder builder = new URIBuilder();
    builder.setScheme("http");
    builder.setHost("xa.xingcloud.com");
    builder.setPort(80);
    builder.setPath("/v4/sof-isafe/wuzijing");
    builder.addParameter("action", "qf.svc.show.5.1.11");
    URI uri = builder.build();
    HttpGet httpGet = new HttpGet(uri);
    System.out.println(uri);
    HttpClient client = HttpClients.createDefault();
    int ok = 0, failed = 0;
    HttpResponse response;
    StatusLine statusLine;
    int cnt = 0;
    for (int i = 0; i < 1000; i++) {
      response = client.execute(httpGet);
      statusLine = response.getStatusLine();
      if (200 == statusLine.getStatusCode()) {
        ++ok;
      } else {
        ++failed;
      }
      ++cnt;
      System.out.println("cnt=" + cnt);
    }

    System.out.println("OK=" + ok + ", Failed=" + failed);
  }
}
