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
import streamlines.jwtsample.common.RedisDao;
import streamlines.jwtsample.exception.ForbiddenException;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    private  final ObjectMapper objectMapper;

    private final RedisDao redisDao;

    @Value("${spring.jwt.key}")
    private String key;
    @Value("${spring.jwt.live.atk}")
    private Long atkLive;

    @Value("${spring.jwt.live.rtk}")
    private Long rtkLive;

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

    //RTK 발급 추가
    public TokenResponse createTokenByLogin(AccountResponse accountResponse) throws JsonProcessingException {
        Subject atkSubject = Subject.atk(
                accountResponse.getAccountId(),
                accountResponse.getEmail(),
                accountResponse.getNickname()
        );

        Subject rtkSubject = Subject.rtk(
                accountResponse.getAccountId(),
                accountResponse.getEmail(),
                accountResponse.getNickname()
        );

        String atk = createToken(atkSubject, atkLive);
        String rtk = createToken(rtkSubject, atkLive);

        redisDao.setValues(accountResponse.getEmail(), rtk, Duration.ofMillis(rtkLive));

        return new TokenResponse(atk, rtk);
    }

    public Subject getSubject(String atk) throws JsonProcessingException {
        String subjectStr = Jwts.parser().setSigningKey(key).parseClaimsJws(atk)
                .getBody().getSubject();

        return objectMapper.readValue(subjectStr, Subject.class);
    }

    public TokenResponse reissueAtk(AccountResponse accountResponse) throws JsonProcessingException {
        String rtkInRedis = redisDao.getValues(accountResponse.getEmail());
        if (Objects.isNull(rtkInRedis)) throw new ForbiddenException("인증 정보가 만료되었습니다.");

        Subject atkSubject = Subject.atk(
                accountResponse.getAccountId(),
                accountResponse.getEmail(),
                accountResponse.getNickname()
        );

        String atk = createToken(atkSubject, atkLive);
        return new TokenResponse(atk, null);
    }


}
