package streamlines.jwtsample.account;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

//@Transactional  //db commit 안함
@ExtendWith(MockitoExtension.class)
class AccountServiceTest {


    //AccountService accountService;
    AccountInterface accountInterface;

    @Mock
    AccountRepository accountRepository;

//    @MockBean
//    AccountRepository accountRepository;
//
    PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    @BeforeEach
    void setup() {
        accountInterface = new AccountService(accountRepository, passwordEncoder);
    }


    String email = "user1@test.com";
    String nickname = "user1";

    String password = "user1@pass";

    @Test
    @DisplayName("회원 가입")
    void signUp() {


        //given

        SignUpRequest signUpRequest = new SignUpRequest(email, password, nickname);

        Account account = new Account(
                signUpRequest.getEmail(),
                signUpRequest.getPassword(),
                signUpRequest.getNickname()
        );
        account.setId(2L);

        given(accountRepository.existsByEmail(any())).willReturn(false);
        given(accountRepository.save(any())).willReturn(account);

        //when
        AccountResponse accountResponse = accountInterface.signUp(signUpRequest);

        assertThat(accountResponse.getEmail()).isEqualTo(email);
        assertThat(accountResponse.getNickname()).isEqualTo(nickname);

    }

    @Test
    @DisplayName("로그인")
    void login() {
        //given
        signUp();
        LoginRequest loginRequest = new LoginRequest(email, password);
        Account account = new Account(
                email,
                passwordEncoder.encode(password),
                nickname
        );
        account.setId(2L);

        given(accountRepository.findByEmail(any())).willReturn(Optional.of(account));


        AccountResponse accountResponse = accountInterface.login(loginRequest);

        //then
        assertThat(accountResponse.getEmail()).isEqualTo(email);
        assertThat(accountResponse.getNickname()).isEqualTo(nickname);
    }
}