package fi.rudi.ssoidh.controller;

import fi.rudi.ssoidh.authentication.UserParams;
import fi.rudi.ssoidh.domain.User;
import fi.rudi.ssoidh.domain.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


/**
 * Created by rudi on 18/04/16.
 */
@RestController
@RequestMapping("/rest/users")
public class UserController {
  @Autowired
  UserRepository userRepository;


  @RequestMapping(method = RequestMethod.POST)
  public User create(@RequestBody UserParams params) {
    return userRepository.save(params.toUser());
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(DuplicateKeyException.class)
  public ErrorResponse handleValidationException(DuplicateKeyException e) {
    return new ErrorResponse("email_already_taken", "This email is already taken.");
  }

  @AllArgsConstructor
  private class ErrorResponse {
    private final String code;
    private final String message;
  }
}
