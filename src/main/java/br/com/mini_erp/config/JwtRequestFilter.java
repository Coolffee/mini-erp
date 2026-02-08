package br.com.mini_erp.config;

import br.com.mini_erp.cadastro.UsuarioRepository;
import br.com.mini_erp.shared.JwtUtil;
import br.com.mini_erp.shared.TenantContext;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger; // Importe Logger
import org.slf4j.LoggerFactory; // Importe LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class); // Declaração do logger

    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final UsuarioRepository usuarioRepository;

    public JwtRequestFilter(UserDetailsService userDetailsService,
                            JwtUtil jwtUtil,
                            UsuarioRepository usuarioRepository) {
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;
        Long empresaId = null;

        // 1. Extrair o JWT do cabeçalho
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
                empresaId = jwtUtil.extractEmpresaId(jwt);
            } catch (ExpiredJwtException e) {
                logger.warn("JWT Token expirado para o usuário: " + e.getClaims().getSubject());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token JWT expirado.");
                return;
            } catch (Exception e) {
                logger.error("Erro ao parsear JWT Token: " + e.getMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token JWT inválido.");
                return;
            }
        }

        // 2. Validar o token e configurar segurança
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            if (jwtUtil.validateToken(jwt, username)) {

                // 3. Definir o tenant atual
                if (empresaId != null) {
                    TenantContext.setCurrentTenantId(empresaId); // AGORA ESTE MÉTODO EXISTE E É CHAMADO CORRETAMENTE
                    logger.debug("Tenant ID [" + empresaId + "] definido para o usuário " + username);
                } else {
                    logger.warn("JWT para o usuário " + username + " não contém o ID da empresa.");
                }

                // 4. Configurar autenticação
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                logger.debug("Usuário " + username + " autenticado com sucesso.");
            } else {
                logger.warn("JWT Token inválido para o usuário: " + username);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token JWT inválido.");
                return;
            }
        }

        // 5. Continuar a cadeia de filtros
        try {
            chain.doFilter(request, response);
        } finally {
            // 6. Limpar o contexto do tenant
            TenantContext.clear();
            logger.debug("TenantContext limpo pelo JwtRequestFilter.");
        }
    }
}