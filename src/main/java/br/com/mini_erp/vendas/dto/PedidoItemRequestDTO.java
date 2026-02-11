package br.com.mini_erp.vendas.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class PedidoItemRequestDTO {

    @NotNull
    @Schema(description = "ID do Produto a ser vendido", example = "10")
    private Long produtoId;

    @NotNull
    @Schema(description = "Quantidade a ser vendida", example = "5")
    private Integer quantidade;

    @NotNull
    @Schema(description = "Preço unitário de venda (pode diferir do cadastro)", example = "150.00")
    private BigDecimal precoUnitario;

    // Getters e Setters
    public Long getProdutoId() { return produtoId; }
    public void setProdutoId(Long produtoId) { this.produtoId = produtoId; }
    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
    public BigDecimal getPrecoUnitario() { return precoUnitario; }
    public void setPrecoUnitario(BigDecimal precoUnitario) { this.precoUnitario = precoUnitario; }
}