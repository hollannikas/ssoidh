package fi.rudi.ssoidh.service;

import fi.rudi.ssoidh.domain.User;
import fi.rudi.ssoidh.domain.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Created by rudi on 11/04/16.
 */
@Service
public class SecurityContextServiceImpl implements SecurityContextService {
  @Autowired
  private UserRepository userRepository;

  @Override
  public User currentUser() {
    final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    final Optional<User> currentUser = userRepository.findOneByUsername(authentication.getName());
    return currentUser.orElse(null);
  }
}
