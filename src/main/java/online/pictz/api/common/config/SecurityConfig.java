package online.pictz.api.common.config;

import static org.springframework.http.HttpMethod.POST;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String[] WHITE_STATIC_LIST_URL = {
        "/",
        "/favicon.ico",
        "/css/**",
        "/img/**",
        "/js/**",
        "/images/**",
        "/static/**",
        "/login",
        "/error",
        "/topics/**"
    };

    private static final String[] WHITE_API_LIST_URL = {
        "/api/v1/topics/**",
        "/api/v1/votes/**",
        "/api/v1/choices/**"
    };

    private static final String[] WHITE_CSRF_LIST_URL = {
        "/api/v1/votes/bulk"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf
                .ignoringAntMatchers(WHITE_CSRF_LIST_URL)
            )
            .authorizeRequests(auth -> auth
                .antMatchers(WHITE_STATIC_LIST_URL).permitAll()
                .antMatchers(WHITE_API_LIST_URL).permitAll()
                .anyRequest().authenticated()
            )
            .formLogin().disable()
            .logout(logout -> logout
                .logoutUrl("/api/v1/auth/logout")
                .logoutSuccessHandler(
                    (request, response, authentication) -> SecurityContextHolder.clearContext())
            )
            .build();
    }
}
