package ir.alirezaalijani.news.application.controller;

import capital.scalable.restdocs.AutoDocumentation;
import capital.scalable.restdocs.jackson.JacksonResultHandlers;
import capital.scalable.restdocs.response.ResponseModifyingPreprocessors;
import com.fasterxml.jackson.databind.ObjectMapper;
import ir.alirezaalijani.news.application.domain.model.User;
import ir.alirezaalijani.news.application.domain.model.UserRole;
import ir.alirezaalijani.news.application.domain.repositories.UserRepository;
import ir.alirezaalijani.news.application.domain.request.LoginRequest;
import ir.alirezaalijani.news.application.domain.request.UserAddRequest;
import ir.alirezaalijani.news.application.domain.request.UserUpdateRequest;
import ir.alirezaalijani.news.application.domain.response.JwtResponse;
import ir.alirezaalijani.news.application.domain.response.UserResponse;
import ir.alirezaalijani.news.application.domain.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.cli.CliDocumentation;
import org.springframework.restdocs.http.HttpDocumentation;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@SpringBootTest
@AutoConfigureRestDocs(outputDir = "target/snippets")
class AuthControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    UserRepository userRepository;
    @MockBean
    UserService userService;
    @Autowired
    PasswordEncoder passwordEncoder;

    private MockMvc mockMvc;

    @BeforeEach
    void setup(WebApplicationContext context,
               RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .apply(documentationConfiguration(restDocumentation))
                .alwaysDo(JacksonResultHandlers.prepareJackson(objectMapper))
                .alwaysDo(MockMvcRestDocumentation.document("{class-name}/{method-name}",
                        Preprocessors.preprocessRequest(),
                        Preprocessors.preprocessResponse(
                                ResponseModifyingPreprocessors.replaceBinaryContent(),
                                ResponseModifyingPreprocessors.limitJsonArrayLength(objectMapper),
                                Preprocessors.prettyPrint())))
                .apply(MockMvcRestDocumentation.documentationConfiguration(restDocumentation)
                        .uris()
                        .withScheme("http")
                        .withHost("localhost")
                        .withPort(8080)
                        .and().snippets()
                        .withDefaults(CliDocumentation.curlRequest(),
                                HttpDocumentation.httpRequest(),
                                HttpDocumentation.httpResponse(),
                                AutoDocumentation.requestFields(),
                                AutoDocumentation.responseFields(),
                                AutoDocumentation.pathParameters(),
                                AutoDocumentation.requestParameters(),
                                AutoDocumentation.description(),
                                AutoDocumentation.methodAndPath(),
                                AutoDocumentation.section()))
                .build();
    }

    @WithMockUser(value = "user-test", username = "user-test", roles = "USER")
    @Test
    @DisplayName("Get Users With Role USER Test")
    void getUsersTest() throws Exception {
        var user1 = User.builder().email("user1@gmail.com").name("user1").password(passwordEncoder.encode("123456")).role(UserRole.ROLE_USER).build();
        var user2 = User.builder().email("user2@gmail.com").name("user2").password(passwordEncoder.encode("123456")).role(UserRole.ROLE_USER).build();
        when(userService.getUsers()).thenReturn(Stream.of(user1, user2)
                .map(UserResponse::parentBuilder)
                .collect(Collectors.toList()));

        mockMvc.perform(get("/api/v1/auth/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email", is(user1.getEmail())))
                .andExpect(jsonPath("$[0].name", is(user1.getName())))
                .andExpect(jsonPath("$[0].role", is(user1.getRole().name().replace("ROLE_", ""))))
                .andExpect(jsonPath("$[1].email", is(user2.getEmail())))
                .andExpect(jsonPath("$[1].name", is(user2.getName())))
                .andExpect(jsonPath("$[1].role", is(user2.getRole().name().replace("ROLE_", ""))));

    }

    @DisplayName("Token Base Request Test")
    @Test
    void tokenBaseRequestTests() throws Exception {
        var admin = User.builder().email("admin@gmail.com").name("admin").password(passwordEncoder.encode("123456")).role(UserRole.ROLE_ADMIN).build();
        when(userRepository.findByEmail(admin.getEmail()))
                .thenReturn(Optional.of(admin));
        when(userService.getUser(admin.getEmail())).thenReturn(admin);
        // actual login test
        var result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest("admin@gmail.com", "123456")))
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is(admin.getEmail())))
                .andExpect(jsonPath("$.token", notNullValue()))
                .andReturn();
        var jwtResponse = objectMapper.readValue(result.getResponse().getContentAsString(), JwtResponse.class);

        var user1 = User.builder().email("user1@gmail.com").name("user1").password(passwordEncoder.encode("123456")).role(UserRole.ROLE_USER).build();
        var user2 = User.builder().email("user2@gmail.com").name("user2").password(passwordEncoder.encode("123456")).role(UserRole.ROLE_USER).build();
        when(userService.getUsers()).thenReturn(Stream.of(user1, user2)
                .map(UserResponse::parentBuilder)
                .collect(Collectors.toList()));

        mockMvc.perform(get("/api/v1/auth/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization",jwtResponse.getType().concat(" "))
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/v1/auth/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization",jwtResponse.getType().concat(" ").concat(jwtResponse.getToken()+"d54564d6"))
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/v1/auth/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization",jwtResponse.getType().concat(" ").concat(jwtResponse.getToken()))
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Login Test For Users")
    void loginRequestTestForUsers() throws Exception {
        var admin = User.builder().email("admin@gmail.com").name("admin").password(passwordEncoder.encode("123456")).role(UserRole.ROLE_ADMIN).build();
        when(userRepository.findByEmail(admin.getEmail()))
                .thenReturn(Optional.of(admin));
        when(userService.getUser(admin.getEmail())).thenReturn(admin);
        // email validation
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new LoginRequest("admin", "{non}123456")))
        ).andExpect(status().isBadRequest());

        // password length validation
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new LoginRequest("admin@gmail.com", "12")))
        ).andExpect(status().isBadRequest());

        // not fund user login
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new LoginRequest("alireza@gmail.com", "123456")))
        ).andExpect(status().isUnauthorized());

        // wrong password login
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new LoginRequest("admin@gmail.com", "315546")))
        ).andExpect(status().isUnauthorized());

        // actual login test
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest("admin@gmail.com", "123456")))
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is(admin.getEmail())))
                .andExpect(jsonPath("$.token", notNullValue()));
    }

    @Test
    @DisplayName("Register New User By Admin")
    @WithMockUser(username = "admin@gmail.com", roles = "ADMIN")
    void registerUser() throws Exception {
        var newUser = User.builder().email("user1@gmail.com").name("user1").password(passwordEncoder.encode("password123")).role(UserRole.ROLE_USER).build();
        var addRequest = new UserAddRequest(newUser.getEmail(), "password123", newUser.getName());
        var addRequestExistUser = new UserAddRequest("user2@gmail.com", "password123", newUser.getName());

        when(userService.addNewUser(addRequest)).thenReturn(newUser);
        when(userService.userExist("user2@gmail.com")).thenReturn(true);

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addRequestExistUser))
        ).andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addRequest))
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is(newUser.getEmail())))
                .andExpect(jsonPath("$.role", is("USER")))
                .andExpect(jsonPath("$.name", is(newUser.getName())));
    }

    @Test
    @DisplayName("Update User Self Password")
    @WithMockUser(username = "user1@gmail.com",roles = "USER")
    void updateUserPasswordTest() throws Exception {
        // user mock in repository
        var updateUser = User.builder().email("user1@gmail.com").name("user1").password(passwordEncoder.encode("password123")).role(UserRole.ROLE_USER).build();

        when(userService.userExist(updateUser.getEmail())).thenReturn(true);
        // mock repository save action
        when(userService.updateUser(ArgumentMatchers.any(UserUpdateRequest.class))).thenReturn(updateUser);

        // update request mock request filed -> password length
        mockMvc.perform(put("/api/v1/auth/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserUpdateRequest("user1@gmail.com", "123")))
                ).andExpect(status().isBadRequest());

        // update request mock request filed user2 not the owner
        mockMvc.perform(put("/api/v1/auth/update")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new UserUpdateRequest("user2@gmail.com", "124564653")))
        ).andExpect(status().isBadRequest());

        // update request mock request
        mockMvc.perform(put("/api/v1/auth/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserUpdateRequest("user1@gmail.com", "123456789")))
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is(updateUser.getEmail())));

    }
}