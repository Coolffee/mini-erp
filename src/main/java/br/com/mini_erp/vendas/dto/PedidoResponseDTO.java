package br.com.mini_erp.vendas.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class PedidoResponseDTO {

    private Long id;
    private Long clienteId;
    private String clienteNome; // Para exibição
    private LocalDate dataPedido;
    private List<PedidoItemResponseDTO> itens;
    private BigDecimal valorTotal; // Campo calculado para o valor total do pedido
    private Long empresaId;
    private String empresaNome;

    // Construtores, Getters e Setters
    public PedidoResponseDTO() {}

    public PedidoResponseDTO(Long id, Long clienteId, String clienteNome, LocalDate dataPedido, List<PedidoItemResponseDTO> itens, BigDecimal valorTotal, Long empresaId, String empresaNome) {
        this.id = id;
        this.clienteId = clienteId;
        this.clienteNome = clienteNome;
        this.dataPedido = dataPedido;
        this.itens = itens;
        this.valorTotal = valorTotal;
        this.empresaId = empresaId;
        this.empresaNome = empresaNome;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public String getClienteNome() {
        return clienteNome;
    }

    public void setClienteNome(String clienteNome) {
        this.clienteNome = clienteNome;
    }

    public LocalDate getDataPedido() {
        return dataPedido;
    }

    public void setDataPedido(LocalDate dataPedido) {
        this.dataPedido = dataPedido;
    }

    public List<PedidoItemResponseDTO> getItens() {
        return itens;
    }

    public void setItens(List<PedidoItemResponseDTO> itens) {
        this.itens = itens;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
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

