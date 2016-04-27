package fi.rudi.ssoidh.controller;

import fi.rudi.ssoidh.domain.Comment;
import fi.rudi.ssoidh.domain.Picture;
import fi.rudi.ssoidh.domain.PictureRepository;
import fi.rudi.ssoidh.domain.User;
import fi.rudi.ssoidh.service.SecurityContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * RESTful service for pictures
 * <p>
 * Created by rudi on 01/04/16.
 */
@RestController
@RequestMapping("rest/pictures")
public class PictureController {
  public static final int THUMBNAIL_HEIGHT = 200;
  public static final int THUMBNAIL_WIDTH = 200;

  // TODO: Stream in stead of byte[] / BufferedImage (mem concerns)
  // TODO: Check out good way to document RESTful services
  // TODO: Check that uploaded file is an actual image

  @Autowired
  private PictureRepository repository;

  @Autowired
  private SecurityContextService securityContextService;

  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity<List<Picture>> getPictures() {
    // TODO: Separate data from Picture Jackson JsonView?
    final List<Picture> pictures = StreamSupport
      .stream(repository.findAll(new Sort(Sort.Direction.DESC, "created")).spliterator(), false)
      .collect(Collectors.toList());
    return ResponseEntity.ok().body(pictures);
  }

  // http://stackoverflow.com/questions/16332092/spring-mvc-pathvariable-with-dot-is-getting-truncated
  @RequestMapping(value = "by/{author:.+}", method = RequestMethod.GET)
  public ResponseEntity<List<Picture>> getMyPictures(@PathVariable("author") String author) {

    final List<Picture> pictures = StreamSupport
      .stream(repository.findByOwner(author, new Sort(Sort.Direction.DESC, "created")).spliterator(), false)
      .collect(Collectors.toList());
    return ResponseEntity.ok().body(pictures);
  }



  @RequestMapping(value = "{id}/thumbnail", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
  public ResponseEntity<byte[]> getThumbnail(@PathVariable("id") String id) {
    BufferedImage bufferedImage = getBufferedImage(id);
    return getPictureResponse(bufferedImage, THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT);
  }

  @RequestMapping(value = "{id}/metadata", method = RequestMethod.GET)
  public Picture getMetadata(@PathVariable("id") String id) {
    return repository.findOne(id);
  }

  // TODO get picture size upon upload
  @RequestMapping(value = "{id}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
  public ResponseEntity<byte[]> getPicture(@PathVariable("id") String id) {
    BufferedImage bufferedImage = getBufferedImage(id);
    return getPictureResponse(bufferedImage, bufferedImage.getWidth(), bufferedImage.getHeight());
  }

  @RequestMapping(method = RequestMethod.POST, value = "upload")
  public Picture upload(@RequestParam("file") MultipartFile file,
                        @RequestParam("caption") String caption) {

    User currentUser = securityContextService.currentUser();
    Picture picture = new Picture(caption, currentUser.getUsername(), new Date());
    try {
      picture.setData(file.getBytes());
      repository.save(picture);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return Optional.of(picture).orElse(null);
  }

  // Comments
  @RequestMapping(value = "{id}/comments", method = RequestMethod.GET)
  public List<Comment> getComments(@PathVariable("id") String id) {
    return repository.findOne(id).getComments();
  }


  @RequestMapping(value = "{id}/comments", method = RequestMethod.PUT)
  public Comment addComment(@PathVariable("id") String pictureId,
                            @RequestBody String text) {
    User currentUser = securityContextService.currentUser();
    final Picture picture = repository.findOne(pictureId);
    final Date now = Calendar.getInstance().getTime();
    Comment comment = new Comment(text, currentUser.getName());
    comment.setDate(now);
    picture.getComments().add(comment);
    repository.save(picture);
    return comment;
  }

  @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
  @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
  public void handleNoPermission() {
  }


  private BufferedImage getBufferedImage(@PathVariable("id") String id) {
    final Picture picture = repository.findOne(id);
    InputStream in = new ByteArrayInputStream(picture.getData());
    BufferedImage bufferedImage = null;
    try {
      bufferedImage = ImageIO.read(in);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return bufferedImage;
  }

  private ResponseEntity<byte[]> getPictureResponse(BufferedImage bufferedImage, int width, int height) {
    BufferedImage scaledImage;
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    try {
      if (bufferedImage != null) {
        scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = scaledImage.createGraphics();
        graphics2D.drawImage(bufferedImage, 0, 0, width, height, null);
        graphics2D.dispose();
        ImageIO.write(scaledImage  , "jpg", byteArrayOutputStream);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    byte[] bytes = byteArrayOutputStream.toByteArray();
    return ResponseEntity.ok()
      .contentLength( bytes.length)
      .body(bytes);
  }
}
