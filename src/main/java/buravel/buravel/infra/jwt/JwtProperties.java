package buravel.buravel.infra.jwt;

public interface JwtProperties {
    String SECRET = "buravel";
    int EXPIRATION_TIME = 86400000; // 1일 (1/1000초)
    String TOKEN_PREFIX = "Bearer ";
    String HEADER_STRING = "Authorization";
}