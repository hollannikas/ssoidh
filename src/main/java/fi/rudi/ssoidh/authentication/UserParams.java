package fi.rudi.ssoidh.authentication;

/**
 * Created by rudi on 11/04/16.
 */
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.rudi.ssoidh.domain.User;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

@ToString
@EqualsAndHashCode
public final class UserParams {

  @SuppressWarnings("unused")
  private static final Logger logger = LoggerFactory.getLogger(UserParams.class);

  private final String email;
  private final String password;
  private final String name;

  public UserParams(@JsonProperty("email") String email,
                    @JsonProperty("password") String password,
                    @JsonProperty("name") String name) {
    this.email = email;
    this.password = password;
    this.name = name;
  }

  public Optional<String> getEmail() {
    return Optional.ofNullable(email);
  }

  public Optional<String> getEncodedPassword() {
    return Optional.ofNullable(password).map(p -> new BCryptPasswordEncoder().encode(p));
  }

  public Optional<String> getName() {
    return Optional.ofNullable(name);
  }

  public User toUser() {
    User user = new User();
    user.setUsername(email);
    user.setPassword(new BCryptPasswordEncoder().encode(password));
    user.setName(name);
    return user;
  }

  public UsernamePasswordAuthenticationToken toAuthenticationToken() {
    return new UsernamePasswordAuthenticationToken(email, password);
  }

}
