package fi.rudi.ssoidh.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import fi.rudi.ssoidh.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by rudi on 11/04/16.
 */

class StatelessLoginFilter extends AbstractAuthenticationProcessingFilter {

  private final TokenAuthenticationService tokenAuthenticationService;
  private final UserService userService;

  StatelessLoginFilter(String urlMapping,
                       TokenAuthenticationService tokenAuthenticationService,
                       UserService userService,
                       AuthenticationManager authenticationManager) {
    super(urlMapping);
    this.tokenAuthenticationService = tokenAuthenticationService;
    this.userService = userService;
    setAuthenticationManager(authenticationManager);
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request,
                                              HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
    final UserParams params = new ObjectMapper().readValue(request.getInputStream(), UserParams.class);
    final UsernamePasswordAuthenticationToken loginToken = params.toAuthenticationToken();
    return getAuthenticationManager().authenticate(loginToken);
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request,
                                          HttpServletResponse response,
                                          FilterChain chain,
                                          Authentication authResult) throws IOException, ServletException {
    final UserDetails authenticatedUser = userService.loadUserByUsername(authResult.getName());
    final UserAuthentication userAuthentication = new UserAuthentication(authenticatedUser);

    tokenAuthenticationService.addAuthentication(response, userAuthentication);

    SecurityContextHolder.getContext().setAuthentication(userAuthentication);

    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
  }
}
