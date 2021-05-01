package buravel.buravel.infra;

import buravel.buravel.infra.jwt.JwtAuthenticationFilter;
import buravel.buravel.infra.jwt.JwtAuthorizationFilter;
import buravel.buravel.modules.account.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
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
                .mvcMatchers("/signUp", "/login","/","/tempPassword", "/emailCheck").permitAll()
                .anyRequest().authenticated();

    }
}