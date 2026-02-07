package br.com.mini_erp.shared;

public class TenantContext {

    private static final ThreadLocal<Long> currentTenantId = new ThreadLocal<>();

    public static void setcurrentTenantId(Long tenantId){
        currentTenantId.set(tenantId);
    }

    public static Long getCurrentTenantId() {
        return currentTenantId.get();
    }

    public static void clear(){
            currentTenantId.remove();
        }
}
