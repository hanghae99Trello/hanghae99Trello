package org.sparta.hanghae99trello.config;

import lombok.RequiredArgsConstructor;
import org.sparta.hanghae99trello.jwt.JwtAuthenticationFilter;
import org.sparta.hanghae99trello.jwt.JwtAuthorizationFilter;
import org.sparta.hanghae99trello.jwt.JwtUtil;
import org.sparta.hanghae99trello.security.CustomAccessDeniedHandler;
import org.sparta.hanghae99trello.security.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(securedEnabled = true)
public class WebSecurityConfig {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationConfiguration authenticationConfiguration;


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtUtil);
        filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
        return filter;
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter(jwtUtil, userDetailsService);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf((csrf) -> csrf.disable());

        http.sessionManagement((sessionManagement) ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        http.authorizeHttpRequests((authorizeHttpRequests)->
                authorizeHttpRequests
                        .requestMatchers("/join").permitAll()
                        .requestMatchers("/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/users/**").hasAnyAuthority("AUTH_USER")
                        .requestMatchers(HttpMethod.PUT, "/users/**").hasAnyAuthority("AUTH_USER")
                        .requestMatchers(HttpMethod.GET, "/users/**").hasAnyAuthority("AUTH_USER")
                        .requestMatchers(HttpMethod.DELETE, "/users/**").hasAnyAuthority("AUTH_USER")
                        .anyRequest().authenticated()
        );

        http.exceptionHandling((exceptionHandling) ->
                exceptionHandling
                        .accessDeniedHandler(new CustomAccessDeniedHandler())
        );

        http.addFilterBefore(jwtAuthorizationFilter(), JwtAuthenticationFilter.class);
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}