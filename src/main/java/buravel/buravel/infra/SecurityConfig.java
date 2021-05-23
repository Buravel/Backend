package buravel.buravel.infra;

import buravel.buravel.infra.jwt.JwtAuthenticationFilter;
import buravel.buravel.infra.jwt.JwtAuthorizationFilter;
import buravel.buravel.modules.account.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;

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
                .addFilter(new JwtAuthenticationFilter(authenticationManager()))
                .addFilter(new JwtAuthorizationFilter(authenticationManager(), accountRepository))

                .authorizeRequests()
                .mvcMatchers("/signUp", "/login", "/", "/tempPassword", "/findUsername","/index/search").permitAll()
                .mvcMatchers(HttpMethod.GET, "/plans","/plans/{id}").permitAll()

//                .anyRequest().authenticated();
                .anyRequest().permitAll();
    }

    @Override
    public void configure(WebSecurity web) throws Exception
    {
        web.ignoring()
                .mvcMatchers("/favicon.ico","/resources/**","/error")
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }
}