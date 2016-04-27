package fi.rudi.ssoidh;

import com.jayway.restassured.response.Response;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static com.jayway.restassured.RestAssured.given;

/**
 * Created by rudi on 27/04/16.
 */
public abstract class ITBase {

  protected void uploadPicture(String token, byte[] bytes, int counter) {
    String pictureId = given()
      .request()
      .header("X-AUTH-TOKEN", token)
      .multiPart("file", "myFile", bytes)
      .multiPart("name", "filename")
      .multiPart("caption", "caption" + counter)
      .when()
      .post("rest/pictures/upload")
      .then().extract().body().jsonPath().getString("id");
  }

  protected String authenticate() {
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

    return tokenResponse.getHeader("X-AUTH-TOKEN");
  }

  /**
   * Creates a multivalue map containing a file with a name and a caption
   */
  protected MultiValueMap<String, Object> createUploadMap(String filename, String caption) {
    MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
    byte[] bytes = filename.getBytes();

    map.add("name", filename);
    map.add("caption", caption);
    ByteArrayResource file = new ByteArrayResource(bytes){
      @Override
      public String getFilename(){
        return filename;
      }
    };
    map.add("file", file);
    return map;
  }
}
