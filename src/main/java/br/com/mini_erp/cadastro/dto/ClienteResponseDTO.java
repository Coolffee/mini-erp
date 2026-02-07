package br.com.mini_erp.cadastro.dto;

import java.time.LocalDate;

public class ClienteResponseDTO {

    private Long id;
    private String nome;
    private String email;
    private LocalDate dataNascimento;
    private String documento;
    private Long empresaId; // Incluímos o ID da empresa para visualização, mas não na criação.
    private String empresaNome; // Para facilitar a visualização

    // Construtores, Getters e Setters
    public ClienteResponseDTO() {}

    public ClienteResponseDTO(Long id, String nome, String email, LocalDate dataNascimento, String documento, Long empresaId, String empresaNome) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.dataNascimento = dataNascimento;
        this.documento = documento;
        this.empresaId = empresaId;
        this.empresaNome = empresaNome;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public Long getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(Long empresaId) {
        this.empresaId = empresaId;
    }

    public String getEmpresaNome() {
        return empresaNome;
    }

    public void setEmpresaNome(String empresaNome) {
        this.empresaNome = empresaNome;
    }
}