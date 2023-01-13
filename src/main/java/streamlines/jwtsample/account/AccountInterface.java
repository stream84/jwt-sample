package streamlines.jwtsample.account;

public interface AccountInterface {

    public AccountResponse signUp(SignUpRequest signUpRequest);

    public AccountResponse login(LoginRequest loginRequest);
}
