package com.elex.bigdata.urlrestore.servlet;

import com.elex.bigdata.urlrestore.util.URLRestoreCacheOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * User: Z J Wu Date: 14-2-27 Time: 下午4:32 Package: com.elex.bigdata.urlrestore.servlet
 */
public class URLRestoreServlet extends HttpServlet {
  private static final Logger LOGGER = Logger.getLogger(URLRestoreServlet.class);

  @Override
  protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    String param = StringUtils.trimToNull(req.getParameter("url"));
    PrintWriter pw = resp.getWriter();
    String longURL = null;
    try {
      longURL = URLRestoreCacheOperation.getInstance().restoreURL(param);
    } catch (Exception e) {
      LOGGER.error(e);
    }
    if (StringUtils.isBlank(longURL)) {
      pw.write("err");
    } else {
      pw.write(longURL);
    }
    pw.close();
  }
}
