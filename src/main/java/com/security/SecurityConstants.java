package com.security;

/**
 * Created by Ivan Minchev on 10/31/2017.
 */
public class SecurityConstants {
    public static final String SECRET = "SecretKeyToGenJWTs";
    public static final long EXPIRATION_TIME = 864_000_000; // 10 days
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String LOGIN_URL = "/users/login";
    public static final String REGISTER_URL = "/users/register";
    public static final String REGISTER_ADMINISTRATOR_URL = "/users/administrator/register";
    public static final String CONSOLE_URL = "/h2-console/*";
}
