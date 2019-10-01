package com.example.service.auth.service;

import com.example.service.auth.domain.User;
import com.example.service.auth.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@ComponentScan({"com.medzero.client"})
@Service
public class UserService implements UserDetailsService {

  private static final Logger LOG = LoggerFactory.getLogger(UserService.class);


  private final UserRepository userRepository;

  @Autowired
  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Optional<User> response = userRepository.findByUsername(username);
    if (response.isPresent()) {
      return response.get();
    }
    String msg = "Couldn't find the username " + username + "!";
    LOG.debug(msg);
    throw new UsernameNotFoundException(msg);
  }
}
