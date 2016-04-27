package fi.rudi.ssoidh;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;
import com.lordofthejars.nosqlunit.annotation.UsingDataSet;
import com.lordofthejars.nosqlunit.mongodb.MongoDbRule;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

import static com.jayway.restassured.RestAssured.given;
import static com.lordofthejars.nosqlunit.mongodb.MongoDbRule.MongoDbRuleBuilder.newMongoDbRule;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Integration (REST - mongo) test for PictureController
 *
 * Created by rudi on 02/04/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SSOIDHApplication.class)
@WebAppConfiguration
@Import(ITMongoConfiguration.class)
@IntegrationTest("server.port=9090")
public class PictureControllerIT extends ITBase {

  @Rule
  public MongoDbRule mongoDbRule = newMongoDbRule().defaultSpringMongoDb("integration-test");

  // nosqlunit requirement
  @Autowired
  private ApplicationContext applicationContext;

  private static final String PICTURES_URL = "http://localhost:9090/rest/pictures/";
  private static final String PICTURES_UPLOAD_URL = "http://localhost:9090/rest/pictures/upload";
  private RestTemplate restTemplate = new TestRestTemplate();


  @Value("${local.server.port}")
  private int port;

  @Before
  public void setUp() {
    RestAssured.port = this.port;
  }

  @Test
  public void invalidUserCannotLogin() {
    given()
      .request()
      .body("{ " +
        "\"email\": \"bob\"," +
        "\"password\": \"bob\"" +
        "}")
      .when()
      .post("/api/login")
      .then()
      .statusCode(HttpStatus.UNAUTHORIZED.value());
  }

  @Test
  @UsingDataSet(locations = {"/data/users.json"})
  public void existingUserIsAuthenticated() {
    Response tokenResponse =
      given()
        .request()
        .body("{ " +
          "\"email\": \"bob@bob.com\"," +
          "\"password\": \"bob\"" +
          "}")
        .when()
        .post("/api/login")
        .then()
        .statusCode(HttpStatus.OK.value())
        .extract().response();

    Assert.assertEquals("eyJhbGciOiJIUzUxMiJ9",
      tokenResponse.getHeader("X-AUTH-TOKEN").split("[.]")[0]);
  }

  @Test
  @UsingDataSet(locations = {"/data/users.json"})
  public void unauthenticatedUserCannotUploadAPicture() throws Exception {
    final byte[] bytes = IOUtils.toByteArray(getClass().getResourceAsStream("/data/hanami.jpg"));

    given()
      .request()
      .header("X-AUTH-TOKEN", "")
      .multiPart("file", "myFile", bytes)
      .multiPart("name", "filename")
      .when()
      .post("rest/pictures/upload/caption")
      .then()
      .statusCode(HttpStatus.FORBIDDEN.value());
  }

  @Test
  @UsingDataSet(locations = {"/data/users.json"})
  public void authenticatedUserCanUploadAPicture() throws Exception {
    final String token = authenticate();

    final byte[] bytes = IOUtils.toByteArray(getClass().getResourceAsStream("/data/hanami.jpg"));

    String pictureId = given()
      .request()
      .header("X-AUTH-TOKEN", token)
      .multiPart("file", "myFile", bytes)
      .multiPart("name", "filename")
      .multiPart("caption", "caption!")
      .when()
      .post("rest/pictures/upload")
      .then().extract().body().jsonPath().getString("id");

    // TODO test that objectId is stored to mongo

  }

  @Test
  @UsingDataSet(locations = {"/data/users.json"})
  public void authenticatedUserCanComment() throws Exception {
    final String token = authenticate();

    final byte[] bytes = IOUtils.toByteArray(getClass().getResourceAsStream("/data/hanami.jpg"));

    String pictureId = given()
      .request()
      .header("X-AUTH-TOKEN", token)
      .multiPart("file", "myFile", bytes)
      .multiPart("name", "filename")
      .multiPart("caption", "caption")
      .when()
      .post("rest/pictures/upload")
      .then().extract().body().jsonPath().getString("id");

    final String COMMENT_TEXT = "This is a comment";

    given()
      .request()
      .header("X-AUTH-TOKEN", token)
      .body(COMMENT_TEXT)
      .when()
      .put("rest/pictures/" + pictureId + "/comments")
      .then()
      .statusCode(HttpStatus.OK.value())
      .assertThat().body("author", equalTo("Bob"))
      .assertThat().body("text", equalTo(COMMENT_TEXT));

  }

  @Test
  public void listPictures() throws Exception {
    final String token = authenticate();
    uploadPictures(token);
  }

  @Test
  @UsingDataSet(locations = {"/data/users.json"})
  public void findPicturesByAuthor() throws Exception {
    final String token = authenticate();
    uploadPictures(token);
    System.err.print(given()
      .request()
      .header("X-AUTH-TOKEN", token)
      .when()
      .get("rest/pictures/by/bob@bob.com")
      .then()
      .extract().body().toString()
    );
  }

  private void uploadPictures(String token) throws IOException {
    final byte[] bytes = IOUtils.toByteArray(getClass().getResourceAsStream("/data/hanami.jpg"));

    for(int counter = 0; counter < 10; counter++) {

      uploadPicture(token, bytes, counter);
    }

    ResponseEntity<List> pictures = restTemplate.getForEntity(PICTURES_URL, List.class);
    assertThat(pictures.getBody().size()).isEqualTo(10);
    pictures.getBody().forEach(picture -> System.out.println(picture));
  }

}
