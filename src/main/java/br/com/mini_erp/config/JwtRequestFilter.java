package br.com.mini_erp.config;

import br.com.mini_erp.cadastro.UsuarioRepository;
import br.com.mini_erp.shared.JwtUtil;
import br.com.mini_erp.shared.TenantContext;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

    private final UserDetailsService userDetailsService; // Será implementado para carregar o usuário
    private final JwtUtil jwtUtil;
    private final UsuarioRepository usuarioRepository; // Para carregar o usuário real e sua empresa

    public JwtRequestFilter(UserDetailsService userDetailsService, JwtUtil jwtUtil, UsuarioRepository usuarioRepository) {
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
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
                empresaId = jwtUtil.extractEmpresaId(jwt); // Extrai o ID da empresa do token
            } catch (ExpiredJwtException e) {
                logger.warn("JWT Token expirado para o usuário: " + e.getClaims().getSubject());
                // Poderíamos retornar um erro 401 Unauthorized mais específico aqui
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token JWT expirado.");
                return;
            } catch (Exception e) {
                logger.error("Erro ao parsear JWT Token: " + e.getMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token JWT inválido.");
                return;
            }
        }

        // 2. Validar o token e configurar o contexto de segurança
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Carrega o usuário do banco de dados (UserDetails padrão do Spring Security)
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            if (jwtUtil.validateToken(jwt, username)) {
                // 3. Configurar o TenantContext com o ID da empresa do token
                if (empresaId != null) {
                    TenantContext.setCurrentTenantId(empresaId);
                    logger.debug("Tenant ID [" + empresaId + "] set from JWT for user " + username);
                } else {
                    logger.warn("JWT para o usuário " + username + " não contém o ID da empresa.");
                    // Opcional: Rejeitar requisições se o empresaId for nulo no token para rotas protegidas
                }

                // Configura a autenticação no Spring Security
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                logger.debug("User " + username + " authenticated successfully.");

            } else {
                logger.warn("JWT Token inválido para o usuário: " + username);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token JWT inválido.");
                return;
            }
        }

        // 4. Continuar a cadeia de filtros
        try {
            chain.doFilter(request, response);
        } finally {
            // 5. Limpar o TenantContext, garantindo que não vaze informações entre requisições
            TenantContext.clear();
            logger.debug("TenantContext cleared by JwtRequestFilter.");
        }
    }
}
