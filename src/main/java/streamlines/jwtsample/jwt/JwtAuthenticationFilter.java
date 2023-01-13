package streamlines.jwtsample.jwt;

import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;


@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final AccountDetailsService accountDetailsService;

    public JwtAuthenticationFilter(JwtProvider jwtProvider, AccountDetailsService accountDetailsService) {
        this.jwtProvider = jwtProvider;
        this.accountDetailsService = accountDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        System.out.println("JwtAuthenticationFilter.doFilterInternal");

        String authorization = request.getHeader("Authorization");
        if(!Objects.isNull(authorization)) {
            String atk = authorization.substring(7);
            log.info("request Authorization atk: {}",atk);

            try {
                Subject subject = jwtProvider.getSubject(atk);
                String requestURI = request.getRequestURI();
                if(subject.getType().equals("RTK") && !requestURI.equals("/account/reissue")) { //RTK 토큰인 경우 /account/reissue 에서만 사용한다.
                    throw new JwtException("토큰을 확인하세요");
                }

                UserDetails userDetails =
                        accountDetailsService.loadUserByUsername(subject.getEmail());

                log.info("userDetails Authorities {}", userDetails.getAuthorities());

                UsernamePasswordAuthenticationToken token =
                        new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(token);
            } catch (JwtException e) {
                request.setAttribute("exception", e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}
