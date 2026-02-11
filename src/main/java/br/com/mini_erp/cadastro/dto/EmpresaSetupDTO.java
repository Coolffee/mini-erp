package br.com.mini_erp.cadastro.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmpresaSetupDTO(
        @NotBlank(message = "Nome da empresa é obrigatório")
        String nomeEmpresa,

        @NotBlank(message = "Email do admin é obrigatório")
        @Email(message = "Email inválido")
        String emailAdmin,

        @NotBlank(message = "Senha é obrigatória")
        String senhaAdmin
) {}