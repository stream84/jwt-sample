package streamlines.jwtsample.account;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import streamlines.jwtsample.jwt.JwtProvider;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@Transactional
@ExtendWith(SpringExtension.class)
class AccountControllerTest {

    @InjectMocks
    private AccountController accountController;

    private MockMvc mockMvc;

    @Mock
    private AccountInterface accountInterface;

    @Mock
    private JwtProvider jwtProvider;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(accountController).build();
    }

    @Test
    @DisplayName("회원 가입 API 테스트")
    void signUp() throws Exception {
        //given
        SignUpRequest signUpRequest = SignUpRequest.builder()
                .email("user1@test.com")
                .password("teste1234")
                .nickname("user1").build();

        ArgumentCaptor<SignUpRequest> argumentCaptor = ArgumentCaptor.forClass(SignUpRequest.class);

        //when
        ResultActions resultActions = mockMvc.perform(post("/account/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpRequest))
                );

        //then
        verify(accountInterface).signUp(argumentCaptor.capture());
        resultActions.andExpect(status().isOk());
    }

    @Test
    @DisplayName("회원 가입 API 테스트 not-valid : 이메일 형식이 아님")
    void signUp_not_valid() throws Exception {
        //given
        SignUpRequest signUpRequest = SignUpRequest.builder()
                .email("user1")
                .password("test1234")
                .nickname("user1").build();

        //when
        ResultActions resultActions = mockMvc.perform(post("/account/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest))
        );

        //then
        verifyNoInteractions(accountInterface); //해당 서비스가 실행되지 않음을 확인함
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("회원 가입 API 테스트 not-valid : null")
    void signUp_not_valid_null() throws Exception {
        //given
        SignUpRequest signUpRequest = SignUpRequest.builder().build();

        //when
        ResultActions resultActions = mockMvc.perform(post("/account/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest))
        );

        //then
        verifyNoInteractions(accountInterface); //해당 서비스가 실행되지 않음을 확인함
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("로그인 API 테스트")
    void login() throws Exception {

        LoginRequest loginRequest = LoginRequest.builder()
                .email("user1@test.com")
                .password("Teste@1234")
                .build();
        //given
        //로그인
        ResultActions resultActions = mockMvc.perform(post("/account/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))
                );

        ArgumentCaptor<LoginRequest> argumentCaptor = ArgumentCaptor.forClass(LoginRequest.class);
        verify(accountInterface).login(argumentCaptor.capture());
        resultActions.andExpect(status().isOk());
    }

    @Test
    @DisplayName("로그인 API 테스트 : 이메일 형식 아님")
    void login_not_valid_email() throws Exception {

        LoginRequest loginRequest = LoginRequest.builder()
                .email("user1")
                .password("teste1234")
                .build();
        //given
        //로그인
        ResultActions resultActions = mockMvc.perform(post("/account/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
        );

        verifyNoInteractions(accountInterface);
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("로그인 API 테스트 : 패스워드 형식 아님")
    void login_not_valid_password() throws Exception {

        LoginRequest loginRequest = LoginRequest.builder()
                .email("user1@user.com")
                .password("teste1234")
                .build();
        //given
        //로그인
        ResultActions resultActions = mockMvc.perform(post("/account/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
        );

        verifyNoInteractions(accountInterface);
        resultActions.andExpect(status().isBadRequest());
    }


    public static boolean isValidInet4Address(String ip)
    {
        String[] groups = ip.split("\\.");

        if (groups.length != 4) {
            return false;
        }

        try {
            return Arrays.stream(groups)
                    .filter(s -> s.length() > 1 && s.startsWith("0"))
                    .map(Integer::parseInt)
                    .filter(i -> (i >= 0 && i <= 255))
                    .count() == 4;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Test
    void test1() {

        String ip ="Unknown";
        final String INET4ADDRESS = "172.8.9.28";

//        Assertions.assertThat(isValidInet4Address(ip)).isFalse();
//        Assertions.assertThat(isValidInet4Address("1.1.1.1")).isTrue();

//        boolean validInet4Address = isValidInet4Address("172.8.7.28");
//        System.out.println("validInet4Address = " + validInet4Address);

        // `InetAddressValidator` 가져오기
        InetAddressValidator validator = InetAddressValidator.getInstance();

        // IPv4 주소 확인
        if (validator.isValidInet4Address(INET4ADDRESS)) {
            System.out.print("The IP address " + INET4ADDRESS + " is valid");
        }
        else {
            System.out.print("The IP address " + INET4ADDRESS + " isn't valid");
        }

    }
}