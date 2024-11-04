package online.pictz.api.common.config;

import static org.springframework.http.HttpMethod.POST;

import lombok.RequiredArgsConstructor;
import online.pictz.api.user.service.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

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
        "/api/v1/votes/bulk",
        "/api/v1/topic_suggests"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .formLogin().disable()
            .httpBasic().disable()
            .csrf(csrf -> csrf
                .ignoringAntMatchers(WHITE_CSRF_LIST_URL)
            )
            .authorizeRequests(auth -> auth
                .antMatchers(WHITE_STATIC_LIST_URL).permitAll()
                .antMatchers(WHITE_API_LIST_URL).permitAll()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/api/v1/admin/**").hasRole("ADMIN")
                .antMatchers("login").anonymous()
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .userInfoEndpoint()
                .userService(customOAuth2UserService)
                .and()
                .defaultSuccessUrl("/", true)
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout", POST.name()))
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
            )
            .build();
    }

}
