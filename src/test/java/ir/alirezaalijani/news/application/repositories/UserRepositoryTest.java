package ir.alirezaalijani.news.application.repositories;

import ir.alirezaalijani.news.application.domain.model.User;
import ir.alirezaalijani.news.application.domain.model.UserRole;
import ir.alirezaalijani.news.application.domain.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void saveUserTest(){
        var user= User.builder().email("user@mail.com")
                .name("user")
                .role(UserRole.ROLE_USER)
                .password("password")
                .build();
        var insertedUser=userRepository.save(user);
        assertThat(insertedUser.getId()).isGreaterThan(0);
    }

    @Test
    void findByEmailTest() {
    }

    @Test
    void findAllByRoleTest() {
    }

    @Test
    void existsByEmailTest() {
    }
}