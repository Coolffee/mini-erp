package br.com.mini_erp.estoque;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    @Query("select sum(p.quantidadeEstoque) from Produto p")
    Integer totalEmEstoque();
}
