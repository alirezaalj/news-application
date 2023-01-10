package ir.alirezaalijani.news.application.domain.service;

import ir.alirezaalijani.news.application.domain.model.User;
import ir.alirezaalijani.news.application.domain.request.UserAddRequest;
import ir.alirezaalijani.news.application.domain.request.UserUpdateRequest;
import ir.alirezaalijani.news.application.domain.response.UserResponse;

import java.util.List;

public interface UserService {
    User getUser(String username);
    List<UserResponse> getUsers();
    boolean userExist(String username);
    User addNewUser(UserAddRequest request);
    User updateUser(UserUpdateRequest request);
}
