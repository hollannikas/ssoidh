package fi.rudi.ssoidh.controller;

import fi.rudi.ssoidh.domain.Comment;
import fi.rudi.ssoidh.domain.Picture;
import fi.rudi.ssoidh.domain.PictureRepository;
import fi.rudi.ssoidh.domain.User;
import fi.rudi.ssoidh.service.SecurityContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.imageio.ImageIO;
import javax.websocket.server.PathParam;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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
  public List<Picture> getPictures() {
    // TODO: Separate data from Picture Jackson JsonView?
    return repository.findAll();
  }

  @RequestMapping(value = "{id}/thumbnail", method = RequestMethod.GET, produces = "image/png")
  public BufferedImage getThumbnail(@PathVariable("id") String id) {
    final Picture picture = repository.findOne(id);
    InputStream in = new ByteArrayInputStream(picture.getData());
    BufferedImage scaledImage = null;
    BufferedImage bufferedImage;
    try {
      bufferedImage = ImageIO.read(in);

        if (bufferedImage != null) {
          scaledImage = new BufferedImage(THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT, BufferedImage.TYPE_INT_RGB);
          Graphics2D graphics2D = scaledImage.createGraphics();
          graphics2D.drawImage(bufferedImage, 0, 0, THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT, null);
          graphics2D.dispose();
        }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return Optional.of(scaledImage).orElse(null);
  }

  @RequestMapping(value = "{id}/metadata", method = RequestMethod.GET)
  public Picture getMetadata(@PathVariable("id") String id) {
    return repository.findOne(id);
  }

  @RequestMapping(value = "{id}", method = RequestMethod.GET, produces = "image/png")
  public BufferedImage getPicture(@PathVariable("id") String id) {
    final Picture picture = repository.findOne(id);
    InputStream in = new ByteArrayInputStream(picture.getData());
    BufferedImage bufferedImage = null;
    try {
      bufferedImage = ImageIO.read(in);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return Optional.of(bufferedImage).orElse(null);
  }


  @RequestMapping(method = RequestMethod.POST, value = "upload/{caption}")
  public Picture upload(@RequestParam("name") String name,
                                 @RequestParam("file") MultipartFile file,
                                 @PathParam("caption") String caption,
                                 RedirectAttributes redirectAttributes) {

    User currentUser = securityContextService.currentUser();
    Picture picture = new Picture(caption, currentUser.getUsername());
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


  @RequestMapping(value = "{id}/comments/add/{text}", method = RequestMethod.GET)
  public Comment addComment(@PathVariable("id") String pictureId,
                             @PathVariable("text") String commentText) {
    User currentUser = securityContextService.currentUser();
    final Picture picture = repository.findOne(pictureId);
    final Date now = Calendar.getInstance().getTime();
    Comment comment = new Comment(commentText, currentUser.getName());
    comment.setDate(now);
    picture.getComments().add(comment);
    return comment;
  }

  @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
  @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
  public void handleNoPermission() {
  }
}
