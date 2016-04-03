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
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * RESTful service for pictures
 * <p>
 * Created by rudi on 01/04/16.
 */
@RestController
@Path("pictures")
@Produces(MediaType.APPLICATION_JSON)
public class PictureController {

  // TODO: Stream in stead of byte[] / BufferedImage (mem concerns)
  // TODO: Check out good way to document RESTful services
  // TODO: Check that uploaded file is an actual image

  @Autowired
  private PictureRepository repository;

  @GET
  public List<String> getPictures() {
    return StreamSupport.stream(repository.findAll().spliterator(), false)
      .map(Picture::getId)
      .collect(Collectors.toList());
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
