package streamlines.jwtsample.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import streamlines.jwtsample.account.AccountResponse;

import javax.annotation.PostConstruct;
import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    private  final ObjectMapper objectMapper;

    @Value("${spring.jwt.key}")
    private String key;
    @Value("${spring.jwt.live.atk}")
    private Long atkLive;

    @PostConstruct
    protected void init() {
        key = Base64.getEncoder().encodeToString(key.getBytes());
    }

    public String createToken(Subject subject, Long tokenLive) throws JsonProcessingException {
        String subjectStr = objectMapper.writeValueAsString(subject);   //json to string
        Claims claims = Jwts.claims()
                .setSubject(subjectStr);
        Date date = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(date)
                .setExpiration(new Date(date.getTime() + tokenLive))
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
    }

    public TokenResponse createTokenByLogin(AccountResponse accountResponse) throws JsonProcessingException {
        Subject atkSubject = Subject.atk(
                accountResponse.getAccountId(),
                accountResponse.getEmail(),
                accountResponse.getNickname()
        );
        String atk = createToken(atkSubject, atkLive);
        return new TokenResponse(atk, null);
    }

    public Subject getSubject(String atk) throws JsonProcessingException {
        String subjectStr = Jwts.parser().setSigningKey(key).parseClaimsJws(atk)
                .getBody().getSubject();

        return objectMapper.readValue(subjectStr, Subject.class);
    }


}
