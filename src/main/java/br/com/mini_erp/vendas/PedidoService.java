package br.com.mini_erp.vendas;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository repository;

    public PedidoService(PedidoRepository repository) {
        this.repository = repository;
    }

    public List<Pedido> listarTodos() {
        return repository.findAll();
    }

    public Pedido salvar(Pedido pedido) {
        return repository.save(pedido);
    }

    public Pedido buscarPorId(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new RuntimeException("Pedido não encontrado")
        );
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }
}
