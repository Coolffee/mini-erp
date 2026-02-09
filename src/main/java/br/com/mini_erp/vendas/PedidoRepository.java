package br.com.mini_erp.vendas;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import java.math.BigDecimal;
import java.time.LocalDate; // Importe LocalDate
import java.util.List;      // Importe List

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    @Query("""
        select sum(i.preco * i.quantidade)
        from PedidoItem i
    """)
    BigDecimal totalVendido();

    long countByClienteId(Long clienteId);

    // Busca todos os pedidos cuja data está entre dataInicio e dataFim
    List<Pedido> findByDataPedidoBetween(LocalDate dataInicio, LocalDate dataFim);
}