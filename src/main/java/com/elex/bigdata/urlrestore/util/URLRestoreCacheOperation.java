package com.elex.bigdata.urlrestore.util;

import com.elex.bigdata.ro.BasicRedisOperation;
import com.elex.bigdata.ro.RedisOperationException;
import com.elex.bigdata.urlrestore.exception.URLRestoreException;
import com.elex.bigdata.urlrestore.model.URLResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * User: Z J Wu Date: 14-2-27 Time: 下午4:35 Package: com.elex.bigdata.urlrestore.servlet
 */
public class URLRestoreCacheOperation {
  private static final Logger LOGGER = Logger.getLogger(URLRestoreCacheOperation.class);
  private static final String HTTP_SCHEMA = "http";
  private static final String HOST = "goo.mx";
  private static final int PORT = 80;
  private static final String PATH = "/api/s2l";

  private final ObjectMapper RESULT_MAPPER = new ObjectMapper();

  private final String HTTP = "http://";
  private final int EXPIRE = 86400;
  private static URLRestoreCacheOperation INSTANCE;
  private final HttpClient CLIENT = HttpClients.createDefault();

  public synchronized static URLRestoreCacheOperation getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new URLRestoreCacheOperation();
    }
    return INSTANCE;
  }

  private URLRestoreCacheOperation() {
  }

  private final BasicRedisOperation bro = new BasicRedisOperation("/redis.site.properties");

  protected String readResponse(HttpEntity responseEntity) throws IOException {
    if (responseEntity == null) {
      return null;
    }
    StringBuilder responseString = new StringBuilder();
    String line;
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(responseEntity.getContent()));) {
      while ((line = reader.readLine()) != null) {
        line = StringUtils.trimToNull(line);
        if (StringUtils.isNotEmpty(line)) {
          responseString.append(line);
        }
      }
    }
    return responseString.toString();
  }

  private String doRestore(String shortURL) throws URLRestoreException, URISyntaxException, IOException {
    URIBuilder builder = new URIBuilder();
    builder.setScheme(HTTP_SCHEMA);
    builder.setHost(HOST);
    builder.setPort(PORT);
    builder.setPath(PATH);
    builder.addParameter("url", shortURL);

    URI uri = builder.build();
    HttpGet httpGet = new HttpGet(uri);
    HttpResponse response = CLIENT.execute(httpGet);
    StatusLine statusLine = response.getStatusLine();
    int c = statusLine.getStatusCode();
    URLResult urlResult;
    if (200 == c) {
      urlResult = toURLResult(readResponse(response.getEntity()));
      if (urlResult.isValid()) {
        return urlResult.getUrl();
      } else {
        throw new URLRestoreException("Invalid url result - " + shortURL);
      }
    } else {
      throw new URLRestoreException("Error return code: " + String.valueOf(c));
    }
  }

  private URLResult toURLResult(String resultString) throws IOException, URLRestoreException {
    return new URLResult(RESULT_MAPPER.readValue(resultString, Map.class));
  }

  public String restoreURL(String shortURL) throws RedisOperationException, URISyntaxException, IOException,
    URLRestoreException {
    if (StringUtils.isBlank(shortURL)) {
      return null;
    }
    String inputURL;
    if (!shortURL.startsWith("http")) {
      inputURL = HTTP.concat(shortURL);
    } else {
      inputURL = shortURL;
    }

    String cachedURL = bro.get(inputURL);
    if (StringUtils.isNotBlank(cachedURL)) {
      LOGGER.info("[URL-RESTORE] - Using cache(" + inputURL + ").");
      return cachedURL;
    }

    String outURL = doRestore(inputURL);
    bro.set(inputURL, outURL, EXPIRE);
    return outURL;
  }
}
