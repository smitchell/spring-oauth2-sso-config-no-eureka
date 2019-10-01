package com.example.service.auth.validation;

public class ValidationRegex {
  public static final String UUID_PATTERN = "^[0-9A-Fa-f]{8}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{12}$";
  public static final String BCRYPT_PATTERN = "^\\$2[ayb]\\$.{56}$";

  private ValidationRegex() {
  }
}
