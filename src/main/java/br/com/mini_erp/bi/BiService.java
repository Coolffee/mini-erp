package br.com.mini_erp.bi;

import br.com.mini_erp.cadastro.ClienteRepository;
import br.com.mini_erp.estoque.ProdutoRepository;
import br.com.mini_erp.vendas.PedidoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class BiService {

    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;
    private final PedidoRepository pedidoRepository;

    public BiService(
            ClienteRepository clienteRepository,
            ProdutoRepository produtoRepository,
            PedidoRepository pedidoRepository
    ) {
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
        this.pedidoRepository = pedidoRepository;
    }

    public long totalClientes() {
        return clienteRepository.count();
    }

    public Integer totalProdutosEmEstoque() {
        return produtoRepository.totalEmEstoque();
    }

    public BigDecimal totalVendido() {
        return pedidoRepository.totalVendido();
    }

    public long pedidosPorCliente(Long clienteId) {
        return pedidoRepository.countByClienteId(clienteId);
    }
}
