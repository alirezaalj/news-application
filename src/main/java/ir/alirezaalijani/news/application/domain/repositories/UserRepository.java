package ir.alirezaalijani.news.application.domain.repositories;

import ir.alirezaalijani.news.application.domain.model.User;
import ir.alirezaalijani.news.application.domain.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Integer> {
    Optional<User> findByEmail(String username);
    List<User> findAllByRole(UserRole role);
    boolean existsByEmail(String username);
}
