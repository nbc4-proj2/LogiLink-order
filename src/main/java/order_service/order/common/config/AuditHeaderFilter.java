package order_service.order.common.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AuditHeaderFilter extends OncePerRequestFilter {

    // ThreadLocal로 요청별 userId를 보관
    public static final ThreadLocal<Long> AUDIT_UID = new ThreadLocal<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String userIdHeader = request.getHeader("X-User-Id");
            if (userIdHeader != null && !userIdHeader.isBlank()) {
                AUDIT_UID.set(Long.valueOf(userIdHeader));
            }

            // 다음 필터/컨트롤러 실행
            filterChain.doFilter(request, response);
        } finally {
            // 요청 처리 완료 후 ThreadLocal 정리
            AUDIT_UID.remove();
        }
    }

}
