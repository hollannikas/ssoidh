package fi.rudi.ssoidh;

import com.github.fakemongo.Fongo;
import com.mongodb.Mongo;
import fi.rudi.ssoidh.domain.PictureRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Integration test mongo configuration for Fongo
 * <p>
 * Created by rudi on 03/04/16.
 */
@Configuration
@EnableMongoRepositories
@ComponentScan(basePackageClasses = {PictureRepository.class})
@PropertySource("classpath:application.yml")
public class ITMongoConfiguration extends AbstractMongoConfiguration {

  @Override
  protected String getDatabaseName() {
    return "integration-test";
  }

  @Override
  public
  @Bean
  Mongo mongo() {
    // uses fongo for in-memory tests
    return new Fongo("integration-test").getMongo();
  }

  @Override
  protected String getMappingBasePackage() {
    return "fi.rudi.ssoidh.domain";
  }

}
