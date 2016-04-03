package fi.rudi.ssoidh.domain;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Basic Mongo repository for Pictures
 *
 * Created by rudi on 01/04/16.
 */
public interface PictureRepository extends MongoRepository<Picture, String> {
}
