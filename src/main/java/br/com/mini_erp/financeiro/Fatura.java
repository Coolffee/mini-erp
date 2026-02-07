package br.com.mini_erp.financeiro;

import br.com.mini_erp.vendas.Pedido;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.Filter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "faturas")
@Filter(name = "tenantFilter", condition = "pedido.empresa_id = :tenantId")
public class Fatura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Pedido é obrigatório")
    @OneToOne
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @NotNull(message = "Valor total é obrigatório")
    private BigDecimal valorTotal;

    @Enumerated(EnumType.STRING)
    private StatusFatura status = StatusFatura.PENDENTE;

    private LocalDate dataEmissao = LocalDate.now();

    public Fatura() {}

    public Fatura(Pedido pedido, BigDecimal valorTotal) {
        this.pedido = pedido;
        this.valorTotal = valorTotal;
    }

    // getters e setters

    public Long getId() {
        return id;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public StatusFatura getStatus() {
        return status;
    }

    public void setStatus(StatusFatura status) {
        this.status = status;
    }

    public LocalDate getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(LocalDate dataEmissao) {
        this.dataEmissao = dataEmissao;
    }
}
