package fi.rudi.ssoidh.configuration;

import fi.rudi.ssoidh.controller.PictureController;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

import javax.ws.rs.ApplicationPath;

/**
 * Register controllers to the Jersey configuration
 * Created by rudi on 01/04/16.
 */
@Configuration
@ApplicationPath("/rest")
public class JerseyConfiguration extends ResourceConfig {
  public JerseyConfiguration() {
    register(PictureController.class);
    register(MultiPartFeature.class);
  }
}
