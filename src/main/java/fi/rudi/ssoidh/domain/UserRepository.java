package fi.rudi.ssoidh.domain;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

/**
 * Basic Mongo repository for Users
 *
 * Created by rudi on 01/04/16.
 */
public interface UserRepository extends MongoRepository<User, String> {

  Optional<User> findOneByUsername(String username);
}
