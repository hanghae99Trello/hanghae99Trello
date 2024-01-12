package org.sparta.hanghae99trello.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.sparta.hanghae99trello.dto.LoginRequestDto;
import org.sparta.hanghae99trello.message.ErrorMessage;
import org.sparta.hanghae99trello.message.SuccessMessage;
import org.sparta.hanghae99trello.security.UserAuthEnum;
import org.sparta.hanghae99trello.security.UserDetailsImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Slf4j(topic = "로그인 및 JWT 생성")
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static final String LOGIN_ATTEMPT_LOG = "로그인 시도";
    private static final String LOGIN_SUCCESS_AND_JWT_TOKEN_CREATION_LOG = "로그인 성공 및 JWT 생성";
    private static final String LOGIN_FAIL_LOG = "로그인 실패";
    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
        setFilterProcessesUrl("/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.info(LOGIN_ATTEMPT_LOG);
        try {
            LoginRequestDto requestDto = new ObjectMapper().readValue(request.getInputStream(), LoginRequestDto.class);

            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            requestDto.getEmail(),
                            requestDto.getPassword(),
                            null
                    )
            );
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        log.info(LOGIN_SUCCESS_AND_JWT_TOKEN_CREATION_LOG);

        String email = ((UserDetailsImpl) authResult.getPrincipal()).getUsername();
        UserAuthEnum auth = ((UserDetailsImpl) authResult.getPrincipal()).getUser().getAuth();

        String token = jwtUtil.createToken(email, auth);
        jwtUtil.addJwtToCookie(token, response);

        response.getWriter().write(SuccessMessage.LOGIN_SUCCESS_MESSAGE.getSuccessMessage());
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        log.info(LOGIN_FAIL_LOG);
        response.setStatus(401);
        response.getWriter().write(ErrorMessage.PASSWORD_MISMATCH_ERROR_MESSAGE.getErrorMessage());
    }
}