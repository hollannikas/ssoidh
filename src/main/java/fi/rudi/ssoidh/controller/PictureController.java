package fi.rudi.ssoidh.controller;

import fi.rudi.ssoidh.domain.Picture;
import fi.rudi.ssoidh.domain.PictureRepository;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * RESTful service for pictures
 * <p>
 * Created by rudi on 01/04/16.
 */
@RestController
@Path("pictures")
@Produces(MediaType.APPLICATION_JSON)
public class PictureController {
  public static final int THUMBNAIL_HEIGHT = 150;
  public static final int THUMBNAIL_WIDTH = 150;

  // TODO: Stream in stead of byte[] / BufferedImage (mem concerns)
  // TODO: Check out good way to document RESTful services
  // TODO: Check that uploaded file is an actual image

  @Autowired
  private PictureRepository repository;

  @GET
  public List<Picture> getPictures() {
    // TODO: Separate data from Picture
    return repository.findAll();
  }

  @GET
  @Path("{id}/thumbnail")
  @Produces("image/png")
  public Response getThumbnail(@PathParam("id") String id) {
    final Picture picture = repository.findOne(id);
    InputStream in = new ByteArrayInputStream(picture.getData());
    BufferedImage bufferedImage;
    try {
      bufferedImage = ImageIO.read(in);
      if(bufferedImage.getWidth() > THUMBNAIL_WIDTH || bufferedImage.getHeight() > THUMBNAIL_HEIGHT) {
        BufferedImage scaledImage = null;
        if (bufferedImage != null) {
          scaledImage = new BufferedImage(THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT, BufferedImage.TYPE_INT_RGB);
          Graphics2D graphics2D = scaledImage.createGraphics();
          graphics2D.drawImage(bufferedImage, 0, 0, THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT, null);
          graphics2D.dispose();
        }
        return Response.ok(scaledImage).build();
      }
      return Response.ok(bufferedImage).build();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return Response.serverError().build();
  }

  @GET
  @Path("{id}")
  @Produces("image/png")
  public Response getPicture(@PathParam("id") String id) {
    final Picture picture = repository.findOne(id);
    InputStream in = new ByteArrayInputStream(picture.getData());
    BufferedImage bufferedImage;
    try {
      bufferedImage = ImageIO.read(in);
      return Response.ok(bufferedImage).build();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return Response.serverError().build();
  }

  @POST
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  public Response uploadPicture(@FormDataParam("file") InputStream file,
                              @FormDataParam("caption") String caption,
                              @FormDataParam("file") FormDataContentDisposition fileDetail) {
    Picture picture = new Picture(caption);
    try {
      picture.setData(IOUtils.toByteArray(file));
      repository.save(picture);
      return Response.ok(picture.getId()).build();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return Response.serverError().build();
  }

}
