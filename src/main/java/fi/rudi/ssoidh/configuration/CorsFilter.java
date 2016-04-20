package fi.rudi.ssoidh.configuration;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet filter to set cross-origin access control
 *
 * Created by rudi on 03/04/16.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFilter implements Filter {

  // TODO set tighter access control
  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) {
    HttpServletResponse response = (HttpServletResponse) res;
    response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
    response.setHeader("Access-Control-Allow-Headers", "X-Requested-With,content-type,X-Auth-Token");
    response.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE");
    response.setHeader("Access-Control-Allow-Credentials", "true");
    try {
      chain.doFilter(req, res);
    } catch (IOException | ServletException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void init(FilterConfig filterConfig) {}

  @Override
  public void destroy() {}

}
