package br.com.mini_erp.cadastro;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

@Entity
@Table(name = "empresas")
// -------------------------------------------------------------------
// Adicione a definição do filtro aqui.
// O nome "tenantFilter" é arbitrário, mas deve ser único.
// "tenantId" é o nome do parâmetro que o filtro espera.
@FilterDef(name = "tenantFilter", parameters = @ParamDef(name = "tenantId", type = Long.class))
// -------------------------------------------------------------------
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome da empresa é obrigatório")
    private String nome;

    // Construtores
    public Empresa() {}

    public Empresa(String nome) {
        this.nome = nome;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
}

