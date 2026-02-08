package br.com.mini_erp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider; // Importe AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider; // Importe DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy; // Importe SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; // Importe UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtRequestFilter jwtRequestFilter;
    private final UserDetailsService userDetailsService; // Nosso UserDetailsServiceImpl

    // Injetar o JwtRequestFilter e UserDetailsService no construtor
    public SecurityConfig(JwtRequestFilter jwtRequestFilter, UserDetailsService userDetailsService) {
        this.jwtRequestFilter = jwtRequestFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Desabilita CSRF (comum para APIs REST)
                .authorizeHttpRequests(auth -> auth
                        // Rotas públicas (não exigem autenticação)
                        .requestMatchers("/usuarios/login").permitAll()
                        .requestMatchers("/teste").permitAll() // Exemplo de rota de teste
                        // Adicione aqui outras rotas públicas, como /empresas para cadastro de empresas
                        // (se você for permitir que qualquer um cadastre uma empresa sem autenticação)
                        // .requestMatchers("/empresas").permitAll()

                        // Todas as outras requisições exigem autenticação
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        // Configura Spring Security para não criar ou usar sessões HTTP
                        // ESSENCIAL para arquiteturas JWT (stateless)
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // Adiciona nosso filtro JWT antes do filtro padrão de autenticação de usuário/senha do Spring
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Bean para configurar o provedor de autenticação com nosso UserDetailsService
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService); // Usa nossa implementação de UserDetailsService
        authProvider.setPasswordEncoder(passwordEncoder()); // Usa nosso PasswordEncoder
        return authProvider;
    }

    // Expõe o AuthenticationManager como um Bean, para que possamos usá-lo em outros lugares (ex: Controller de login)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}