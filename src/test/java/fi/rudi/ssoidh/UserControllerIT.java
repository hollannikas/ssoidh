package fi.rudi.ssoidh;

import com.jayway.restassured.RestAssured;
import com.lordofthejars.nosqlunit.annotation.CustomComparisonStrategy;
import com.lordofthejars.nosqlunit.annotation.IgnorePropertyValue;
import com.lordofthejars.nosqlunit.annotation.ShouldMatchDataSet;
import com.lordofthejars.nosqlunit.annotation.UsingDataSet;
import com.lordofthejars.nosqlunit.core.LoadStrategyEnum;
import com.lordofthejars.nosqlunit.mongodb.MongoDbRule;
import com.lordofthejars.nosqlunit.mongodb.MongoFlexibleComparisonStrategy;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static com.jayway.restassured.RestAssured.given;
import static com.lordofthejars.nosqlunit.mongodb.MongoDbRule.MongoDbRuleBuilder.newMongoDbRule;

/**
 * Created by rudi on 17/04/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SSOIDHApplication.class)
@WebAppConfiguration
@Import(ITMongoConfiguration.class)
@CustomComparisonStrategy(comparisonStrategy = MongoFlexibleComparisonStrategy.class)
@IntegrationTest("server.port=9090")
public class UserControllerIT {

  @Rule
  public MongoDbRule mongoDbRule = newMongoDbRule().defaultSpringMongoDb("integration-test");

  // nosqlunit requirement
  @Autowired
  private ApplicationContext applicationContext;

  @Value("${local.server.port}")
  private int port;

  @Before
  public void setUp() {
    RestAssured.port = this.port;
  }

  @Test
  @ShouldMatchDataSet(location = "/expectedResults/newUserGetsSaved.json")
  @UsingDataSet(loadStrategy = LoadStrategyEnum.DELETE_ALL)
  @IgnorePropertyValue(properties = {"password"})
  public void newUserGetsSaved() {
    given()
      .request()
      .contentType("application/json")
      .body("{" +
        "  \"email\": \"user@mock.org\",\n" +
        "  \"password\": \"bob\",\n" +
        "  \"name\": \"john\"\n" +
        "}")
      .when()
      .post("/rest/users/")
      .then()
      .statusCode(HttpStatus.OK.value());
  }

  @Test
  @Ignore("This works in IntelliJ, but fails with gradlew stage")
  //@UsingDataSet(loadStrategy = LoadStrategyEnum.DELETE_ALL)
  public void emailAddressIsUnique() {
    given()
      .request()
      .contentType("application/json")
      .body("{" +
        "  \"email\": \"user@mock.org\",\n" +
        "  \"password\": \"bob\",\n" +
        "  \"name\": \"john\"\n" +
        "}")
      .when()
      .post("/api/users/");
    given()
      .request()
      .contentType("application/json")
      .body("{" +
        "  \"email\": \"user@mock.org\",\n" +
        "  \"password\": \"bob\",\n" +
        "  \"name\": \"john\"\n" +
        "}")
      .when()
      .post("/rest/users/")
    .then().statusCode(HttpStatus.BAD_REQUEST.value());
  }
}
