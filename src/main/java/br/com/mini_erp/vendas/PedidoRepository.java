package br.com.mini_erp.vendas;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import java.math.BigDecimal;


@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    @Query("""
        select sum(i.preco * i.quantidade)
        from PedidoItem i
    """)
    BigDecimal totalVendido();

    long countByClienteId(Long clienteId);
}
