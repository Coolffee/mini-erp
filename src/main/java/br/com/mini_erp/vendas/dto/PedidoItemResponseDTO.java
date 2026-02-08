package br.com.mini_erp.vendas.dto;

import java.math.BigDecimal;

public class PedidoItemResponseDTO {

    private Long id;
    private Long produtoId;
    private String produtoNome; // Para exibir o nome do produto
    private Integer quantidade;
    private BigDecimal precoUnitario;
    private BigDecimal subtotal; // Preço * Quantidade

    // Não inclui o Pedido aqui para evitar recursão!

    // Construtores, Getters e Setters
    public PedidoItemResponseDTO() {}

    public PedidoItemResponseDTO(Long id, Long produtoId, String produtoNome, Integer quantidade, BigDecimal precoUnitario, BigDecimal subtotal) {
        this.id = id;
        this.produtoId = produtoId;
        this.produtoNome = produtoNome;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
        this.subtotal = subtotal;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(Long produtoId) {
        this.produtoId = produtoId;
    }

    public String getProdutoNome() {
        return produtoNome;
    }

    public void setProdutoNome(String produtoNome) {
        this.produtoNome = produtoNome;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }

    public void setPrecoUnitario(BigDecimal precoUnitario) {
        this.precoUnitario = precoUnitario;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
}
