package fi.rudi.ssoidh.service;

import fi.rudi.ssoidh.domain.User;
import fi.rudi.ssoidh.domain.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Created by rudi on 11/04/16.
 */
@Service
public class UserService implements UserDetailsService {
  @Autowired
  private UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    final Optional<User> user = userRepository.findOneByUsername(username);
    final AccountStatusUserDetailsChecker detailsChecker = new AccountStatusUserDetailsChecker();
    user.ifPresent(detailsChecker::check);
    return user.orElseThrow(() -> new UsernameNotFoundException("user not found."));
  }
}
