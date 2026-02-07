package br.com.mini_erp.config;

import br.com.mini_erp.shared.TenantContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.hibernate.Session;
import org.springframework.stereotype.Component;


@Aspect
@Component
public class TenantFilterAspect {

    @PersistenceContext
    private EntityManager entityManager;
    // garante que o filtro seja ativado para qualquer operação de dados.
    @Around("execution(* org.springframework.data.jpa.repository.JpaRepository+.*(..))")
    public Object applyTenantFilter(ProceedingJoinPoint joinPoint) throws Throwable {
        Long tenantId = TenantContext.getCurrentTenantId();

        if (tenantId != null) {
            Session session = entityManager.unwrap(Session.class);
            // Ativa o filtro que definimos na entidade @FilterDef
            session.enableFilter("tenantFilter")
                    // Seta o parâmetro "tenantId" com o valor do nosso TenantContext
                    .setParameter("tenantId", tenantId);
            session.setDefaultReadOnly(true); // Opcional: boa prática para evitar modificações acidentais ao filtrar
        }

        try {
            return joinPoint.proceed(); // Executa o metodo do repositório original
        } finally {
            // Garante que o filtro seja desativado após a operação
            // para evitar que ele afete outras operações que não deveriam ser filtradas
            // (ex: buscar empresas para login, embora tenhamos handled com TenantContext.clear())
            if (tenantId != null) {
                Session session = entityManager.unwrap(Session.class);
                session.disableFilter("tenantFilter");
            }
        }
    }
}
