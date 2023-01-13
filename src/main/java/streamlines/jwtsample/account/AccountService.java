package streamlines.jwtsample.account;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import streamlines.jwtsample.exception.BadRequestException;

@Service
@RequiredArgsConstructor
public class AccountService implements AccountInterface{

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AccountResponse signUp(SignUpRequest signUpRequest) {
        boolean isExist = accountRepository.existsByEmail(signUpRequest.getEmail());
        if(isExist) throw new BadRequestException("이미 존재하는 이메일 입니다.");

        String encodedPassword = passwordEncoder.encode(signUpRequest.getPassword());

        Account account = new Account(
                signUpRequest.getEmail(),
                encodedPassword,
                signUpRequest.getNickname()
        );

        account = accountRepository.save(account);

        return AccountResponse.of(account);
    }

    @Override
    public AccountResponse login(LoginRequest loginRequest) {
        Account account = accountRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new BadRequestException("아이디 혹은 비밀번호를 확인하세요."));

        boolean matches = passwordEncoder.matches(
                loginRequest.getPassword(),
                account.getPassword()
        );
        if(!matches) throw new BadRequestException("비밀번호를 확인하세요");

        return AccountResponse.of(account);
    }
}
