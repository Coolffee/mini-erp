package br.com.mini_erp.estoque;

import br.com.mini_erp.shared.exception.ResourceNotFoundException; // Importe
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository repository;

    public ProdutoService(ProdutoRepository repository) {
        this.repository = repository;
    }

    public List<Produto> listarTodos() {
        return repository.findAll();
    }

    @Transactional
    public Produto salvar(Produto produto) {
        return repository.save(produto);
    }

    public Produto buscarPorId(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Produto não encontrado com ID: " + id) // <<-- ALTERADO AQUI
        );
    }

    @Transactional
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Produto não encontrado com ID: " + id);
        }
        repository.deleteById(id);
    }
}
