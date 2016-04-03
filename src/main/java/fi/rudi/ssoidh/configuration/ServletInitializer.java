package fi.rudi.ssoidh.configuration;

import fi.rudi.ssoidh.SSOIDHApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;

public class ServletInitializer extends SpringBootServletInitializer {

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
    return application.sources(SSOIDHApplication.class);
  }

}
