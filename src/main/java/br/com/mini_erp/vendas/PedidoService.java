package br.com.mini_erp.vendas;

import br.com.mini_erp.estoque.Produto;
import br.com.mini_erp.estoque.ProdutoRepository;
import br.com.mini_erp.shared.exception.BusinessException; // Importe
import br.com.mini_erp.shared.exception.ResourceNotFoundException; // Importe
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository repository;
    private final ProdutoRepository produtoRepository;

    public PedidoService(PedidoRepository repository, ProdutoRepository produtoRepository) {
        this.repository = repository;
        this.produtoRepository = produtoRepository;
    }

    public List<Pedido> listarTodos() {
        return repository.findAll();
    }

    public Pedido buscarPorId(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Pedido não encontrado com ID: " + id) // <<-- ALTERADO AQUI
        );
    }

    @Transactional
    public Pedido salvar(Pedido pedido) {
        for (PedidoItem item : pedido.getItens()) {
            Produto produto = item.getProduto();
            if (produto.getQuantidadeEstoque() < item.getQuantidade()) {
                throw new BusinessException("Estoque insuficiente para o produto: " + produto.getNome() + ". Quantidade disponível: " + produto.getQuantidadeEstoque()); // <<-- ALTERADO AQUI
            }
            produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - item.getQuantidade());
            produtoRepository.save(produto);
        }
        return repository.save(pedido);
    }

    @Transactional
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Pedido não encontrado com ID: " + id);
        }
        repository.deleteById(id);
    }
}

