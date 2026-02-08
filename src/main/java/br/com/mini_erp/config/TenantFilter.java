package br.com.mini_erp.config;

import br.com.mini_erp.shared.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class TenantFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String tenantIdHeader = request.getHeader("X-Tenant-ID");

            if (tenantIdHeader != null && !tenantIdHeader.isEmpty()) {
                Long tenantId = Long.parseLong(tenantIdHeader);
                TenantContext.setCurrentTenantId(tenantId); // método corrigido
            }

            filterChain.doFilter(request, response);

        } finally {
            TenantContext.clear();
        }
    }
}
