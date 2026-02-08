package br.com.mini_erp.vendas;

import br.com.mini_erp.estoque.Produto;
import br.com.mini_erp.estoque.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importe Transactional
import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository repository;
    private final ProdutoRepository produtoRepository; // Injetar ProdutoRepository

    public PedidoService(PedidoRepository repository, ProdutoRepository produtoRepository) {
        this.repository = repository;
        this.produtoRepository = produtoRepository;
    }

    public List<Pedido> listarTodos() {
        return repository.findAll();
    }

    public Pedido buscarPorId(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new RuntimeException("Pedido não encontrado")
        );
    }

    @Transactional // Garante que a operação de salvar e a baixa de estoque sejam atômicas
    public Pedido salvar(Pedido pedido) {
        // Lógica de baixa de estoque
        for (PedidoItem item : pedido.getItens()) {
            Produto produto = item.getProduto();
            if (produto.getQuantidadeEstoque() < item.getQuantidade()) {
                throw new RuntimeException("Estoque insuficiente para o produto: " + produto.getNome());
            }
            produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - item.getQuantidade());
            produtoRepository.save(produto); // Salva o produto com o estoque atualizado
        }

        // Você pode chamar o FaturaService aqui para gerar a fatura automaticamente
        // (Isso será feito em uma etapa futura de lógica de negócio)

        return repository.save(pedido);
    }

    @Transactional // Deletar também pode ser transacional
    public void deletar(Long id) {
        // Lógica para repor o estoque se o pedido for cancelado ou deletado.
        // Por simplicidade, não vamos repor agora, mas é um requisito de ERP.
        // Se for um "soft delete" (apenas marcar como cancelado), a reposição de estoque é mais complexa.

        Pedido pedido = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        // Se o pedido tiver itens e o estoque precisa ser reposto:
        // for (PedidoItem item : pedido.getItens()) {
        //    Produto produto = item.getProduto();
        //    produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() + item.getQuantidade());
        //    produtoRepository.save(produto);
        // }

        repository.deleteById(id);
    }
}

