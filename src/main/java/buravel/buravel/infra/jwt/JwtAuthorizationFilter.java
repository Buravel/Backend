package buravel.buravel.infra.jwt;

import buravel.buravel.modules.account.Account;
import buravel.buravel.modules.account.AccountRepository;
import buravel.buravel.modules.account.UserAccount;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.Date;
import java.util.List;

/**
 * 인가 필터
 */

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private AccountRepository accountRepository;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, AccountRepository accountRepository) {
        super(authenticationManager);
        this.accountRepository = accountRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String header = request.getHeader(JwtProperties.HEADER_STRING);
        if (header == null || !header.startsWith(JwtProperties.TOKEN_PREFIX)) {
            // 필터타도록
            chain.doFilter(request, response);
            return;
        }
        //그게아니면
        String token = request.getHeader(JwtProperties.HEADER_STRING).replace(JwtProperties.TOKEN_PREFIX, "");
        //만약 토큰이 만료되었으면 401 UNAUTHORIZED
        DecodedJWT decode = JWT.decode(token);
        if (decode.getExpiresAt().before(new Date(System.currentTimeMillis()))) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        JWTVerifier build = JWT.require(Algorithm.HMAC256(JwtProperties.SECRET)).build();

        String username = build.verify(token).getClaim("username").asString();


        if (username != null) {
            Account account = accountRepository.findByUsername(username);
            UserAccount userAccount = new UserAccount(account);
            UsernamePasswordAuthenticationToken userToken = new UsernamePasswordAuthenticationToken(
                    userAccount,
                    account.getPassword(),
                    List.of(new SimpleGrantedAuthority("ROLE_USER"))
            );

            SecurityContextHolder.getContext().setAuthentication(userToken);
        }
        chain.doFilter(request, response);
    }
}