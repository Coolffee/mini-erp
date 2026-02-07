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

    private static final String TENANT_HEADER = "X-Tenant-ID";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException{
     // Obter id da empresa
        try{
            String tenantIdHeader = request.getHeader(TENANT_HEADER);
            Long tenantId = null;

            if(tenantIdHeader != null && !tenantIdHeader.isEmpty()){
                try {
                    tenantId = Long.parseLong(tenantIdHeader);
                    TenantContext.setcurrentTenantId(tenantId);
                    logger.debug("Tenant ID [" + tenantId + "] set in TenantContext for request: " + request.getRequestURI());
                } catch (NumberFormatException e) {
                    logger.warn("Invalid Tenant ID format in header: " + tenantIdHeader);
                    // Opcional: retornar um erro 400 Bad Request aqui
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid " + TENANT_HEADER + " header format.");
                    return;
                }
            } else {
                logger.debug("No Tenant ID header found for request: " + request.getRequestURI());
                //Para rotas públicas (ex: login, registro de empresa), não exigimos tenantId
                //Para rotas protegidas, a validação de segurança (JWT) irá eventualmente rejeitar.
            }

            //Continuar o processamento da requisição
            filterChain.doFilter(request, response);

        } finally {
            //Limpar o TenantContext após a requisição, independente de sucesso ou falha
            TenantContext.clear();
            logger.debug("TenantContext cleared.");
        }
    }
}
