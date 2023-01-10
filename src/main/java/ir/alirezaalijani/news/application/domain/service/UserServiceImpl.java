package ir.alirezaalijani.news.application.domain.service;

import ir.alirezaalijani.news.application.domain.model.User;
import ir.alirezaalijani.news.application.domain.model.UserRole;
import ir.alirezaalijani.news.application.domain.repositories.UserRepository;
import ir.alirezaalijani.news.application.domain.request.UserAddRequest;
import ir.alirezaalijani.news.application.domain.request.UserUpdateRequest;
import ir.alirezaalijani.news.application.domain.response.UserResponse;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService{

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User getUser(String username) {
       return repository.findByEmail(username)
               .orElseThrow(() -> new UsernameNotFoundException(String.format("username %s not exist",username)));
    }

    @Override
    public List<UserResponse> getUsers() {
        return repository.findAllByRole(UserRole.ROLE_USER)
                .stream()
                .map(UserResponse::parentBuilder)
                .collect(Collectors.toList());
    }

    @Override
    public boolean userExist(String username) {
        return  repository.existsByEmail(username);
    }

    @Override
    public User addNewUser(UserAddRequest request) {
        var user = User.builder()
                .email(request.getEmail())
                .name(request.getName())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.ROLE_USER)
                .build();
        return repository.save(user);
    }

    @Override
    public User updateUser(UserUpdateRequest request) {
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException(String.format("username %s not exist",request.getEmail())));
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        return repository.save(user);
    }
}
