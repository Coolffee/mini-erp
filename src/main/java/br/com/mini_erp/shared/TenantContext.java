package br.com.mini_erp.shared;

public class TenantContext {

    private static final ThreadLocal<Long> CURRENT_TENANT = new ThreadLocal<>();

    // Define o tenant atual
    public static void setCurrentTenantId(Long tenantId) { // Este é o método que o JwtRequestFilter espera
        CURRENT_TENANT.set(tenantId);
    }

    // Recupera o tenant atual
    public static Long getCurrentTenantId() { // Este é o método que o TenantFilterAspect espera
        return CURRENT_TENANT.get();
    }

    // Limpa o tenant após a requisição
    public static void clear() {
        CURRENT_TENANT.remove();
    }
}