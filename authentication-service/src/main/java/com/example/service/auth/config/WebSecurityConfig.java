package com.example.service.auth.config;

import com.example.service.auth.filter.JwtAuthenticationFilter;
import com.example.service.auth.filter.JwtAuthorizationFilter;
import com.example.service.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * For configuring the end users recognized by this Authorization Server
 */
@Order(-20)
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  private final UserService userService;
  private final AuthenticationConfiguration authenticationConfiguration;
  private final String privateKey;

  @Autowired
  public WebSecurityConfig(
      @Value("${keyPair.private-key}") final String privateKey,
      final UserService userService,
      final AuthenticationConfiguration authenticationConfiguration) {
    this.privateKey = privateKey;
    this.authenticationConfiguration = authenticationConfiguration;
    this.userService = userService;
  }

  @Override
  public void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth
        .parentAuthenticationManager(authenticationConfiguration.getAuthenticationManager())
        .userDetailsService(userService)
        .passwordEncoder(passwordEncoder());
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Override
  public void configure(WebSecurity web) throws Exception {
    web.ignoring().antMatchers(
        "/webjars/**",
        "/css/**",
        "/images/**",
        "/favicon.ico");
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    // @formatter:off
    http
        .requestMatchers()
            .antMatchers("/",  "/oauth", "/login",  "/api/authenticate", "/oauth/authorize")
            .and()
        .authorizeRequests()
            .anyRequest().authenticated()
            .and()
        .formLogin()
            .loginPage( "/login")
            .permitAll()
            .and()
        .logout()
            .permitAll()
        .and()
            .addFilter(new JwtAuthenticationFilter(privateKey, authenticationManager()))
            .addFilter(new JwtAuthorizationFilter(privateKey, authenticationManager()));
    // @formatter:on
  }

}
