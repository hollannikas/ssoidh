package fi.rudi.ssoidh.service;

import fi.rudi.ssoidh.domain.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Created by rudi on 11/04/16.
 */
@Service
public class UserService implements UserDetailsService {
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = new User();
    user.setName("Mock User");
    user.setUsername("user@mock.org");
    user.setPassword(new BCryptPasswordEncoder().encode("bob"));
    return user;
    /*
    final Optional<User> user = userRepository.findOneByUsername(username);
    final AccountStatusUserDetailsChecker detailsChecker = new AccountStatusUserDetailsChecker();
    user.ifPresent(detailsChecker::check);
    return user.orElseThrow(() -> new UsernameNotFoundException("user not found."));
    */
  }
}
