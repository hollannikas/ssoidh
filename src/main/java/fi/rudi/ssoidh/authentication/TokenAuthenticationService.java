package fi.rudi.ssoidh.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by rudi on 11/04/16.
 */


@Service
class TokenAuthenticationService {

  private static final String AUTH_HEADER_NAME = "x-auth-token";

  private final TokenHandler tokenHandler;

  @Autowired
  public TokenAuthenticationService(TokenHandler tokenHandler) {
    this.tokenHandler = tokenHandler;
  }

  void addAuthentication(HttpServletResponse response,
                         UserAuthentication authentication) {
    final UserDetails user = authentication.getDetails();
    response.addHeader(AUTH_HEADER_NAME, tokenHandler.createTokenForUser(user));
    try {
      response.getWriter().println(response.getHeader(AUTH_HEADER_NAME));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  Authentication getAuthentication(HttpServletRequest request) {
    final String token = request.getHeader(AUTH_HEADER_NAME);
    if (token == null || token.isEmpty()) return null;
    return tokenHandler
      .parseUserFromToken(token)
      .map(UserAuthentication::new)
      .orElse(null);
  }
}
