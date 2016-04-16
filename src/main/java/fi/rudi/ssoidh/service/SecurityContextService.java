package fi.rudi.ssoidh.service;

import fi.rudi.ssoidh.domain.User;

/**
 * Created by rudi on 11/04/16.
 */
public interface SecurityContextService {
  User currentUser();
}
