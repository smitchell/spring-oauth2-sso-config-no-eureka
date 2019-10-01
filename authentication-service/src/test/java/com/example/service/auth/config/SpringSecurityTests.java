package com.example.service.auth.config;

import com.example.service.auth.domain.User;
import com.example.service.auth.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SpringSecurityTests {

  @MockBean
  ClientDetails clientDetails;

  @MockBean
  UserDetails userDetails;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private UserRepository userRepository;

  @Before
  public void before() {
    when(this.userDetails.getPassword())
        .thenReturn(new BCryptPasswordEncoder().encode("password"));
    when(this.userDetails.isEnabled()).thenReturn(Boolean.TRUE);
    when(this.userDetails.isAccountNonLocked()).thenReturn(Boolean.TRUE);
    when(this.userDetails.isAccountNonExpired()).thenReturn(Boolean.TRUE);
    when(this.userDetails.isCredentialsNonExpired()).thenReturn(Boolean.TRUE);
    when(this.userDetails.getUsername()).thenReturn("username");

    Set<String> scopes = new HashSet<>();
    scopes.add("read");
    scopes.add("write");
    Set<String> grants = new HashSet<>();
    grants.add("password");
    grants.add("refresh_token");
    when(this.clientDetails.getScope()).thenReturn(scopes);
    when(this.clientDetails.getAccessTokenValiditySeconds()).thenReturn(100);
    when(this.clientDetails.getAuthorizedGrantTypes()).thenReturn(grants);
    when(this.clientDetails.getRefreshTokenValiditySeconds()).thenReturn(100);
    when(this.clientDetails.getClientId()).thenReturn("dummy-client");
    when(this.clientDetails.isSecretRequired())
        .thenReturn(Boolean.TRUE);
    when(this.clientDetails.getClientSecret())
        .thenReturn(new BCryptPasswordEncoder().encode("client-secret"));

    Optional<User> optional = userRepository.findById(1L);
    if (!optional.isPresent()) {
      User user = new User();
      user.setId(1L);
      user.setFirstName("Test");
      user.setLastName("User");
      user.setUsername("user");
      user.setPassword(new BCryptPasswordEncoder().encode("password"));
      user.setRoles("ROLE_USER, ROLE_ADMIN");
      userRepository.save(user);
    }
  }

  @Test
  public void loginSucceeds() throws Exception {

    MultiValueMap<String, String> form = new LinkedMultiValueMap<String, String>();
    form.set("username", "user");
    form.set("password", "password");

    mockMvc.perform(post("/login")
            .params(form).with(csrf()))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isFound())
            .andExpect(redirectedUrl("/"))
            .andReturn();
  }

  @Test
  public void loginFailure() throws Exception {
    MultiValueMap<String, String> form = new LinkedMultiValueMap<String, String>();
    form.set("username", "notUser");
    form.set("password", "notpassword");

    mockMvc.perform(post("/login")
            .params(form).with(csrf()))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(header().string("Location", "/login?error"))
            .andReturn();
  }


  @Test
  public void oauthToken() throws Exception {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("grant_type", "password");
    params.add("client_id", "dummy-client");
    params.add("client_secret", "client-secret");
    params.add("username", "user");
    params.add("password", "password");

    MvcResult result = mockMvc.perform(post("/oauth/token")
            .with(httpBasic("dummy-client", "client-secret"))
            .params(params))
            .andExpect(status().is2xxSuccessful())
            .andReturn();
  }

  @Test
  public void authorizationRedirects() throws Exception {
    MvcResult result = mockMvc.perform(get("/oauth/authorize"))
            .andExpect(status().isFound())
            .andExpect(header().string("Location", "http://localhost/login"))
            .andReturn();
  }


}
