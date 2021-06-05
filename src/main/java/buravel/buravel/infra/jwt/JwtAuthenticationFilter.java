package buravel.buravel.infra.jwt;

import buravel.buravel.modules.account.Account;
import buravel.buravel.modules.account.dtos.AccountDto;
import buravel.buravel.modules.account.UserAccount;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * 인증필터 // 인증 != 인가(권한부여)
 */
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        // accountDto 파싱
        ObjectMapper om = new ObjectMapper();
        AccountDto accountDto = null;

        // 요청에서 받은 값
        try {
            accountDto = om.readValue(request.getInputStream(), AccountDto.class);
        } catch (IOException e) {
            e.printStackTrace();
        }



        // 유저네임 패스워드 토큰생성 // != jwt토큰  // 유저정보를 담기위한 토큰
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        accountDto.getUsername(),
                        accountDto.getPassword());
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        return authenticate;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult) throws IOException, ServletException {
        UserAccount principal = (UserAccount) authResult.getPrincipal();
        Account account = principal.getAccount();

        String jwtToken = JWT.create()
                .withSubject(account.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis()+JwtProperties.EXPIRATION_TIME))
                .withClaim("id", account.getId())
                .withClaim("username", account.getUsername())
                .sign(Algorithm.HMAC256(JwtProperties.SECRET));
        response.addHeader(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX + jwtToken);

    }
}