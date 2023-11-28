package Sun.crud.utils;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // 특정 엔드포인트에 대해서는 필터를 건너뛰도록 설정
        if (isAllowedPath(request)) {
            chain.doFilter(request, response);
            return;
        }

        // 쿠키에서 엑세스토큰 가져옴
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    String token = cookie.getValue();

                    System.out.println("엑세스 토큰 : " + token);

                    try {
                        if (jwtUtil.isTokenValid(token)) {
                            // 유효한 토큰이면 다음 단계로 진행
                            chain.doFilter(request, response);
                        } else {
                            // 유효하지 않은 토큰에 대한 처리
                            jwtUtil.sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Token is not valid");
                        }
                    } catch (Exception e) {
                        // 예외 처리
                        jwtUtil.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexpected server error occurred");
                    }
                }
            }
        }

        // 쿠키에 "accessToken"이라는 이름의 쿠키가 없는 경우 또는 로그인 요청이 아닌 경우는 여기로 오게 됩니다.
        // 원하는 처리를 추가하세요.
    }


    private boolean isAllowedPath(HttpServletRequest request) {
        // "/login" 엔드포인트에 대해서는 필터를 건너뛰도록 설정
        return "/login".equals(request.getRequestURI());
}
}
