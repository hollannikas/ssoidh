package fi.rudi.ssoidh.domain;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Date;

/**
 * Created by rudi on 06/04/16.
 */
@Getter
@Setter
@RequiredArgsConstructor
public class Comment {
  @NonNull
  private String text;
  @NonNull
  private String author;

  private Date date;

}
