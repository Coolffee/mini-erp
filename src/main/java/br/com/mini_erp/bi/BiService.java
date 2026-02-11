package br.com.mini_erp.bi;

import br.com.mini_erp.cadastro.ClienteRepository;
import br.com.mini_erp.estoque.ProdutoRepository;
import br.com.mini_erp.vendas.Pedido;
import br.com.mini_erp.vendas.PedidoRepository;
import com.opencsv.CSVWriter; // Importe o CSVWriter
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Writer; // Importe o Writer
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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


    // --- NOVO MÉTODO PARA EXPORTAÇÃO CSV ---
    public void escreverVendasCsv(Writer writer, LocalDate dataInicio, LocalDate dataFim) {
        // O TenantFilterAspect já garante que só virão pedidos da empresa logada
        List<Pedido> pedidos = pedidoRepository.findByDataPedidoBetween(dataInicio, dataFim);

        try (CSVWriter csvWriter = new CSVWriter(writer)) {
            // 1. Escreve o cabeçalho do arquivo CSV
            String[] header = {"ID_PEDIDO", "DATA", "ID_CLIENTE", "NOME_CLIENTE", "ID_PRODUTO", "NOME_PRODUTO", "QUANTIDADE", "PRECO_UNITARIO", "SUBTOTAL"};
            csvWriter.writeNext(header);

            // 2. Itera sobre os pedidos para escrever as linhas
            for (Pedido pedido : pedidos) {
                // Para cada pedido, iteramos sobre seus itens
                pedido.getItens().forEach(item -> {
                    BigDecimal subtotal = item.getPreco().multiply(BigDecimal.valueOf(item.getQuantidade()));

                    String[] linha = {
                            String.valueOf(pedido.getId()),
                            pedido.getDataPedido().toString(),
                            String.valueOf(pedido.getCliente().getId()),
                            pedido.getCliente().getNome(),
                            String.valueOf(item.getProduto().getId()),
                            item.getProduto().getNome(),
                            String.valueOf(item.getQuantidade()),
                            item.getPreco().toString(),
                            subtotal.toString()
                    };
                    csvWriter.writeNext(linha);
                });
            }
        } catch (IOException e) {
            // Lançar uma exceção de runtime para que o GlobalExceptionHandler possa capturá-la
            throw new RuntimeException("Erro ao escrever o arquivo CSV", e);
        }
    }
}