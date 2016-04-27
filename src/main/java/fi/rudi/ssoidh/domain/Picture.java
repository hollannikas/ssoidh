package fi.rudi.ssoidh.domain;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * A Picture
 *
 * Created by rudi on 01/04/16.
 */
@Getter
@Setter
@RequiredArgsConstructor
@Document
public class Picture {
  @Id
  private String id;

  @NonNull
  private String caption;

  @NonNull
  private String owner;

  @NonNull
  private Date created;

  private byte[] data;

  private List<Comment> comments = new ArrayList<>();
}
