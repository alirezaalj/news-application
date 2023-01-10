package ir.alirezaalijani.news.application.security.service;

import ir.alirezaalijani.news.application.domain.model.User;
import org.springframework.security.core.Authentication;

public interface TokenService {

    String generateToken(User user);
    String generateToken(Authentication authentication);
    boolean validateToken(String authToken);
    String getTokenSubject(String token);
}
