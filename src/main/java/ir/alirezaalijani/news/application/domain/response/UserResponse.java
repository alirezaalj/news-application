package ir.alirezaalijani.news.application.domain.response;

import ir.alirezaalijani.news.application.domain.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private String email;
    private Date createAt;
    private Date updateAt;
    private String role;
    private String name;

    public static UserResponse parentBuilder(User user){
        return UserResponse.builder()
                .email(user.getEmail())
                .createAt(user.getCreateAt())
                .updateAt(user.getUpdateAt())
                .name(user.getName())
                .role(user.getRole().name().replace("ROLE_","")).build();
    }
}
