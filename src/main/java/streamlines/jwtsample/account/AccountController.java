package streamlines.jwtsample.account;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import streamlines.jwtsample.jwt.JwtProvider;
import streamlines.jwtsample.jwt.TokenResponse;

import javax.validation.Valid;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

    //private final AccountService accountService;
    private final AccountInterface accountInterface;
    private final JwtProvider jwtProvider;

    @PostMapping("/sign-up")
    public AccountResponse signUp(@Valid @RequestBody SignUpRequest signUpRequest){
        System.out.println("AccountController.signUp");
        return accountInterface.signUp(signUpRequest);
    }

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest loginRequest) throws JsonProcessingException {
        AccountResponse accountResponse = accountInterface.login(loginRequest);

        return jwtProvider.createTokenByLogin(accountResponse);
    }

    @GetMapping("/test")
    public String test() {
        return "good!";
    }
}
