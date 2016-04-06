package fi.rudi.ssoidh.domain;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;


/**
 * A Picture
 *
 * Created by rudi on 01/04/16.
 */
@Getter
@Setter
@RequiredArgsConstructor
public class Picture {
  @Id
  private String id;

  @NonNull
  private String caption;

  private byte[] data;

  private List<Comment> comments = new ArrayList<>();
}
