package buravel.buravel.infra;

import buravel.buravel.infra.jwt.JwtAuthenticationFilter;
import buravel.buravel.infra.jwt.JwtAuthorizationFilter;
import buravel.buravel.modules.account.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final AccountRepository accountRepository;
    private final CorsConfig corsConfig;


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .addFilter(corsConfig.corsFilter())
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http
                .formLogin().disable()
                .httpBasic().disable();
        http
                .addFilter(new JwtAuthenticationFilter(authenticationManager(), accountRepository))
                .addFilter(new JwtAuthorizationFilter(authenticationManager(), accountRepository))
                .authorizeRequests()
<<<<<<< HEAD
                .mvcMatchers("/signUp", "/login","/","/tempPassword", "/emailCheck").permitAll()
=======
                .mvcMatchers("/signUp", "/login","/","/tempPassword").permitAll()
                .mvcMatchers("/emailVerification","/emailCheckToken").access("hasRole('ROLE_USER')")
>>>>>>> 7e2d8fd0efdf08791f5a0f57b482616da8c8e942
                .anyRequest().authenticated();

    }

    @Override
    public void configure(WebSecurity web) throws Exception
    {
        web.ignoring()
                .mvcMatchers("/favicon.ico","/resources/**","/error")
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }
}