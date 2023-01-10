package ir.alirezaalijani.news.application.domain.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAddRequest {
    @Email
    private String email;
    @Size(min = 5,max = 15)
    private String password;
    @Size(min = 1,max = 50)
    private String name;
}
