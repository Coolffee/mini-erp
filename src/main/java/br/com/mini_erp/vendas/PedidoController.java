package br.com.mini_erp.vendas;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import jakarta.validation.Valid;
import br.com.mini_erp.cadastro.Cliente;
import br.com.mini_erp.cadastro.ClienteRepository;
import br.com.mini_erp.cadastro.Empresa;
import br.com.mini_erp.cadastro.EmpresaRepository;
import br.com.mini_erp.estoque.Produto;
import br.com.mini_erp.estoque.ProdutoRepository;
import br.com.mini_erp.shared.TenantContext;
import br.com.mini_erp.vendas.dto.PedidoItemRequestDTO;
import br.com.mini_erp.vendas.dto.PedidoItemResponseDTO;
import br.com.mini_erp.vendas.dto.PedidoRequestDTO;
import br.com.mini_erp.vendas.dto.PedidoResponseDTO;
import org.springframework.http.ResponseEntity;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private static final Logger logger =
            LoggerFactory.getLogger(PedidoController.class);

    private final PedidoService service;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;
    private final EmpresaRepository empresaRepository; // Injetar EmpresaRepository

    public PedidoController(
            PedidoService service,
            ClienteRepository clienteRepository,
            ProdutoRepository produtoRepository,
            EmpresaRepository empresaRepository
    ) {
        this.service = service;
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
        this.empresaRepository = empresaRepository;
    }

    @GetMapping
    public List<PedidoResponseDTO> listarTodos() {
        return service.listarTodos().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> buscarPorId(@PathVariable Long id) {
        Pedido pedido = service.buscarPorId(id);
        return ResponseEntity.ok(toResponseDTO(pedido));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoResponseDTO criar(@Valid @RequestBody PedidoRequestDTO pedidoRequestDTO) {
        // Obter o ID da empresa do contexto (garantido pelo TenantFilter)
        Long empresaId = TenantContext.getCurrentTenantId();
        if (empresaId == null) {
            throw new RuntimeException("Tenant ID não encontrado no contexto. O login ou o cabeçalho X-Tenant-ID podem estar faltando.");
        }

        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada para o Tenant ID: " + empresaId));

        Cliente cliente = clienteRepository.findById(pedidoRequestDTO.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        // Verificar se o cliente pertence à empresa logada (garantido pelo TenantFilter, mas boa prática validar)
        if (!cliente.getEmpresa().getId().equals(empresaId)) {
            throw new RuntimeException("Cliente não pertence à empresa logada.");
        }

        Pedido pedido = toEntity(pedidoRequestDTO, cliente, empresa);
        Pedido pedidoSalvo = service.salvar(pedido); // O service agora cuidará da baixa de estoque
        return toResponseDTO(pedidoSalvo);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }

    // --- Métodos de Conversão (Mapper) ---

    private PedidoResponseDTO toResponseDTO(Pedido pedido) {
        List<PedidoItemResponseDTO> itemDTOs = pedido.getItens().stream()
                .map(item -> new PedidoItemResponseDTO(
                        item.getId(),
                        item.getProduto().getId(),
                        item.getProduto().getNome(),
                        item.getQuantidade(),
                        item.getPreco(),
                        item.getPreco().multiply(BigDecimal.valueOf(item.getQuantidade()))
                ))
                .collect(Collectors.toList());

        // Calcular valor total do pedido
        BigDecimal valorTotal = itemDTOs.stream()
                .map(PedidoItemResponseDTO::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new PedidoResponseDTO(
                pedido.getId(),
                pedido.getCliente().getId(),
                pedido.getCliente().getNome(),
                pedido.getDataPedido(),
                itemDTOs,
                valorTotal,
                pedido.getEmpresa() != null ? pedido.getEmpresa().getId() : null,
                pedido.getEmpresa() != null ? pedido.getEmpresa().getNome() : null
        );
    }

    private Pedido toEntity(PedidoRequestDTO dto, Cliente cliente, Empresa empresa) {
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setEmpresa(empresa); // Associar a empresa

        List<PedidoItem> itens = dto.getItens().stream()
                .map(itemDTO -> {
                    Produto produto = produtoRepository.findById(itemDTO.getProdutoId())
                            .orElseThrow(() -> new RuntimeException("Produto não encontrado com ID: " + itemDTO.getProdutoId()));

                    // Verificar se o produto pertence à empresa logada
                    if (!produto.getEmpresa().getId().equals(empresa.getId())) {
                        throw new RuntimeException("Produto " + produto.getNome() + " não pertence à empresa logada.");
                    }

                    // No momento da criação do pedido, o preço do item deve vir do DTO ou do produto?
                    // Para um ERP, geralmente o preço é pego do produto no momento da venda,
                    // mas pode ser sobrescrito se houver políticas de desconto.
                    // Por enquanto, vamos usar o preço do DTO, mas valide com o preço do produto.
                    if (itemDTO.getPrecoUnitario().compareTo(produto.getPreco()) != 0) {
                        logger.warn("Preço unitário do item " + produto.getNome() + " no DTO (" + itemDTO.getPrecoUnitario() + ") difere do preço do produto (" + produto.getPreco() + "). Usando o preço do DTO.");
                    }


                    PedidoItem item = new PedidoItem();
                    item.setProduto(produto);
                    item.setQuantidade(itemDTO.getQuantidade());
                    item.setPreco(itemDTO.getPrecoUnitario()); // Usando o preço do DTO, pode ser ajustado
                    item.setPedido(pedido); // Importante para o cascade
                    return item;
                })
                .collect(Collectors.toList());

        pedido.setItens(itens);
        return pedido;
    }
}
