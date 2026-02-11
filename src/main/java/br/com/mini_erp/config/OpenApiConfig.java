package br.com.mini_erp.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;


@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Mini ERP Intelligence API",
                version = "1.0.0",
                description = "API simulando um Add-on Sankhya com Vendas, Estoque, Financeiro e BI.",
                contact = @Contact(name = "Seu Nome", email = "seuemail@exemplo.com")
        ),
        // Aplica a segurança "bearerAuth" globalmente para toda a API
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Insira o token JWT gerado no endpoint /usuarios/login"
)
public class OpenApiConfig {
}