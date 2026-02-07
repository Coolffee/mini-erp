package br.com.mini_erp.vendas;

import br.com.mini_erp.cadastro.Cliente;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import br.com.mini_erp.cadastro.Empresa;
import org.hibernate.annotations.Filter;


@Entity
@Table(name = "pedidos")
@Filter(name = "tenantFilter", condition = "empresa_id = :tenantId")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "empresa_id")
    private Empresa empresa;


    @NotNull(message = "Cliente é obrigatório")
    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    private LocalDate dataPedido = LocalDate.now();

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PedidoItem> itens = new ArrayList<>();

    // Construtores
    public Pedido() {}

    public Pedido(Cliente cliente, List<PedidoItem> itens) {
        this.cliente = cliente;
        this.itens = itens;
        this.itens.forEach(item -> item.setPedido(this));
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public LocalDate getDataPedido() { return dataPedido; }
    public void setDataPedido(LocalDate dataPedido) { this.dataPedido = dataPedido; }

    public List<PedidoItem> getItens() { return itens; }
    public void setItens(List<PedidoItem> itens) {
        this.itens.clear();
        if (itens != null) {
            this.itens.addAll(itens);
            this.itens.forEach(item -> item.setPedido(this));
        }
    }
}
