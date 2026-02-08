package br.com.mini_erp.vendas;

import br.com.mini_erp.estoque.Produto;
import br.com.mini_erp.estoque.ProdutoRepository;
import br.com.mini_erp.financeiro.FaturaService; // Importe FaturaService
import br.com.mini_erp.shared.exception.BusinessException;
import br.com.mini_erp.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository repository;
    private final ProdutoRepository produtoRepository;
    private final FaturaService faturaService; // Injetar FaturaService

    public PedidoService(PedidoRepository repository, ProdutoRepository produtoRepository, FaturaService faturaService) {
        this.repository = repository;
        this.produtoRepository = produtoRepository;
        this.faturaService = faturaService; // Injetar FaturaService
    }

    public List<Pedido> listarTodos() {
        return repository.findAll();
    }

    public Pedido buscarPorId(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Pedido não encontrado com ID: " + id)
        );
    }

    @Transactional // Garante que a operação de salvar pedido, baixa de estoque e geração de fatura sejam atômicas
    public Pedido salvar(Pedido pedido) {
        // 1. Lógica de baixa de estoque
        for (PedidoItem item : pedido.getItens()) {
            Produto produto = item.getProduto();
            if (produto.getQuantidadeEstoque() < item.getQuantidade()) {
                throw new BusinessException("Estoque insuficiente para o produto: " + produto.getNome() + ". Quantidade disponível: " + produto.getQuantidadeEstoque());
            }
            produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - item.getQuantidade());
            produtoRepository.save(produto); // Salva o produto com o estoque atualizado
        }

        // 2. Salva o pedido
        Pedido pedidoSalvo = repository.save(pedido);

        // 3. Gera a fatura automaticamente após salvar o pedido
        faturaService.gerarFatura(pedidoSalvo); // <<-- CHAMADA PARA GERAR FATURA AUTOMATICAMENTE

        return pedidoSalvo;
    }

    @Transactional
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Pedido não encontrado com ID: " + id);
        }
        // Em um ERP real, a exclusão de um pedido geralmente implica em
        // estorno de estoque e cancelamento da fatura.
        // Por simplicidade aqui, estamos apenas deletando o pedido.
        // A fatura permanecerá (pode ser cancelada manualmente via FaturaController).
        repository.deleteById(id);
    }
}

