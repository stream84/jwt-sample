package streamlines.jwtsample.account;

import com.sun.istack.NotNull;
import javax.validation.constraints.Email;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequest {

    @NotNull
    @Email
    private String email;
    @NotNull
    private String password;
    @NotNull
    private String nickname;
}
