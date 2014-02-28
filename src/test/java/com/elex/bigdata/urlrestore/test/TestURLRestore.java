package com.elex.bigdata.urlrestore.test;

import com.elex.bigdata.ro.RedisOperationException;
import com.elex.bigdata.urlrestore.exception.URLRestoreException;
import com.elex.bigdata.urlrestore.util.URLRestoreCacheOperation;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * User: Z J Wu Date: 14-2-27 Time: 下午6:12 Package: com.elex.bigdata.urlrestore.test
 */
public class TestURLRestore {

  @Test
  public void test() throws RedisOperationException, IOException, URLRestoreException, URISyntaxException {
    URLRestoreCacheOperation operation = URLRestoreCacheOperation.getInstance();
    System.out.println(operation.restoreURL("goo.mx/raYNBj"));
  }
}
