package br.com.mini_erp.estoque;

import org.springframework.stereotype.Service;
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

    public Produto salvar(Produto produto) {
        return repository.save(produto);
    }

    public Produto buscarPorId(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new RuntimeException("Produto não encontrado")
        );
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }
}
