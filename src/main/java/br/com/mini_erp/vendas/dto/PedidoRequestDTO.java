package br.com.mini_erp.vendas.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public class PedidoRequestDTO {

    @NotNull(message = "ID do cliente é obrigatório")
    private Long clienteId;

    @Valid // Garante que as validações dentro de PedidoItemRequestDTO também serão aplicadas
    @NotNull(message = "A lista de itens do pedido é obrigatória")
    @Size(min = 1, message = "O pedido deve ter pelo menos um item")
    private List<PedidoItemRequestDTO> itens;

    // Construtores, Getters e Setters
    public PedidoRequestDTO() {}

    public PedidoRequestDTO(Long clienteId, List<PedidoItemRequestDTO> itens) {
        this.clienteId = clienteId;
        this.itens = itens;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public List<PedidoItemRequestDTO> getItens() {
        return itens;
    }

    public void setItens(List<PedidoItemRequestDTO> itens) {
        this.itens = itens;
    }
}
