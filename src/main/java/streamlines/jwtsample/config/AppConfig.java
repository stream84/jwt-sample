package streamlines.jwtsample.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import streamlines.jwtsample.jwt.AccountDetailsService;
import streamlines.jwtsample.jwt.JwtAuthenticationFilter;
import streamlines.jwtsample.jwt.JwtProvider;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class AppConfig {

    private final JwtProvider jwtProvider;
    private final AccountDetailsService accountDetailsService;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    //패스워드 해시 암호화
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    /*
    * cors와 csrf를 disable
    * authorizeRequests()를 통해 인증된 사용자만 접근
    * */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.cors().and().csrf().disable()

                .exceptionHandling()
                .authenticationEntryPoint(customAuthenticationEntryPoint)
                .and()

                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()

                .authorizeRequests()
                .antMatchers("/account/sign-up", "/account/login").permitAll()
                .anyRequest().authenticated()
                .and()

                .addFilterBefore( new JwtAuthenticationFilter( jwtProvider, accountDetailsService),
                        UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }
}
