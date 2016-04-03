package fi.rudi.ssoidh;

import com.lordofthejars.nosqlunit.mongodb.MongoDbRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static com.lordofthejars.nosqlunit.mongodb.MongoDbRule.MongoDbRuleBuilder.newMongoDbRule;
import static org.assertj.core.api.Assertions.assertThat;

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
public class PictureControllerIT {

  @Rule
  public MongoDbRule mongoDbRule = newMongoDbRule().defaultSpringMongoDb("integration-test");

  // nosqlunit requirement
  @Autowired
  private ApplicationContext applicationContext;

  public static final String PICTURES_URL = "http://localhost:9090/rest/pictures/";
  private RestTemplate restTemplate = new TestRestTemplate();

  @Test
  public void uploadPicture() {

    final String caption = "test1";
    final String filename = "screenshot.png";

    final MultiValueMap<String, Object> uploadMap = createUploadMap(filename, caption);
    String objectId = restTemplate.postForObject(PICTURES_URL, uploadMap, String.class);

    // TODO test that objectId is stored to mongo

  }

  @Test
  public void listPictures() {
    for(int counter = 0; counter < 10; counter++) {
      final MultiValueMap<String, Object> uploadMap = createUploadMap("name" + counter, "caption" + counter);
      restTemplate.postForObject(PICTURES_URL, uploadMap, String.class);
    }

    ResponseEntity<List> pictures = restTemplate.getForEntity(PICTURES_URL, List.class);
    assertThat(pictures.getBody().size()).isEqualTo(10);
    pictures.getBody().forEach(picture -> System.out.println(picture));
  }


  /**
   * Creates a multivalue map containing a file with a name and a caption
   */
  private MultiValueMap<String, Object> createUploadMap(String filename, String caption) {
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
