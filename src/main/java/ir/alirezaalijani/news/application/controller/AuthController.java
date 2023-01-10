package ir.alirezaalijani.news.application.controller;

import ir.alirezaalijani.news.application.domain.error.exception.BadRequestException;
import ir.alirezaalijani.news.application.domain.error.exception.BadRequestFieldException;
import ir.alirezaalijani.news.application.domain.model.User;
import ir.alirezaalijani.news.application.domain.request.LoginRequest;
import ir.alirezaalijani.news.application.domain.request.UserAddRequest;
import ir.alirezaalijani.news.application.domain.request.UserUpdateRequest;
import ir.alirezaalijani.news.application.domain.response.JwtResponse;
import ir.alirezaalijani.news.application.domain.response.UserResponse;
import ir.alirezaalijani.news.application.domain.service.UserService;
import ir.alirezaalijani.news.application.security.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@Slf4j
@RequestMapping("/api/v1/auth")
@RestController
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final TokenService tokenService;
    @Value("${application.security.jwt.expiration:86400000}")
    private long tokenExp;

    public AuthController(AuthenticationManager authenticationManager,
                          UserService userService, TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.tokenService = tokenService;
    }

    @GetMapping("users")
    public ResponseEntity<?> getUsers() {
        return ResponseEntity.ok(userService.getUsers());
    }

    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest request,
                                   BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BadRequestFieldException("there is required field.", bindingResult);
        }
        log.info("Login request {}", request.getEmail());
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwtMeta = tokenService.generateToken(authentication);
        User user = userService.getUser(authentication.getName());
        return ResponseEntity.ok(JwtResponse.builder()
                .email(user.getEmail())
                .token(jwtMeta)
                .type("Bearer").name(user.getName())
                .expireAt(System.currentTimeMillis() + tokenExp-1000)
                .build()
        );
    }

    @PostMapping("register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserAddRequest request,
                                          BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BadRequestFieldException("there is required field.", bindingResult);
        }
        if (userService.userExist(request.getEmail())) {
            bindingResult.rejectValue("email", "400", "This Email already exist");
            throw new BadRequestFieldException("there is required field.", bindingResult);
        }
        var user = userService.addNewUser(request);
        if (user != null)
            return ResponseEntity.ok(UserResponse.parentBuilder(user));
        throw new BadRequestException(this.getClass(), "Add new User failed pleas try again");
    }

    @PutMapping("update")
    public ResponseEntity<?> updateUser(Principal principal, @Valid @RequestBody UserUpdateRequest request,
                                        BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BadRequestFieldException("there is required field.", bindingResult);
        }
        if (request.getEmail().equalsIgnoreCase(principal.getName())) {
            if (userService.userExist(principal.getName())) {
                request.setEmail(principal.getName());
                var user = userService.updateUser(request);
                if (user != null)
                    return ResponseEntity.ok(UserResponse.parentBuilder(user));
                throw new BadRequestException(this.getClass(), "Update User failed pleas try again");
            }
        }
        bindingResult.rejectValue("email", "400", "This Email not exist");
        throw new BadRequestFieldException("there is required field.", bindingResult);
    }
}
