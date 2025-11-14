package order_service.order.common.config;

import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

public class HeaderAuditorAware implements AuditorAware<Long> {

    @Override
    public Optional<Long> getCurrentAuditor() {
        // 요청 ThreadLocal에 저장된 X-User-Id를 가져옴
        return Optional.ofNullable(AuditHeaderFilter.AUDIT_UID.get());
    }

}
