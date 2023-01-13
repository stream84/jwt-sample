package streamlines.jwtsample.account;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginRequest {
    private final String email;
    private final String password;

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
