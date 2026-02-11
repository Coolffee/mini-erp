package br.com.mini_erp.vendas;

import br.com.mini_erp.cadastro.Cliente;
import br.com.mini_erp.cadastro.ClienteRepository;
import br.com.mini_erp.cadastro.Empresa;
import br.com.mini_erp.cadastro.EmpresaRepository;
import br.com.mini_erp.estoque.Produto;
import br.com.mini_erp.estoque.ProdutoRepository;
import br.com.mini_erp.shared.TenantContext;
import br.com.mini_erp.vendas.dto.PedidoItemResponseDTO;
import br.com.mini_erp.vendas.dto.PedidoRequestDTO;
import br.com.mini_erp.vendas.dto.PedidoResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/pedidos")
@Tag(name = "2. Gestão de Vendas", description = "Fluxo transacional de Vendas, Estoque e Faturamento")
public class PedidoController {

    private static final Logger logger = LoggerFactory.getLogger(PedidoController.class);

    private final PedidoService service;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;
    private final EmpresaRepository empresaRepository;

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

    @Operation(summary = "Listar Pedidos", description = "Retorna todos os pedidos da empresa logada.")
    @GetMapping
    public List<PedidoResponseDTO> listarTodos() {
        return service.listarTodos().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Buscar Pedido por ID", description = "Detalhes completos de um pedido específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido encontrado."),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> buscarPorId(@PathVariable Long id) {
        Pedido pedido = service.buscarPorId(id);
        return ResponseEntity.ok(toResponseDTO(pedido));
    }

    @Operation(
            summary = "Criar Novo Pedido",
            description = "Inicia uma transação que: 1. Cria o Pedido, 2. Baixa o Estoque, 3. Gera a Fatura Financeira."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pedido criado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Erro de validação (ex: estoque insuficiente, cliente inválido)."),
            @ApiResponse(responseCode = "404", description = "Cliente ou Produto não encontrado.")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoResponseDTO criar(@Valid @RequestBody PedidoRequestDTO pedidoRequestDTO) {
        // Obter o ID da empresa do contexto (garantido pelo TenantFilter)
        Long empresaId = TenantContext.getCurrentTenantId();

        // Em produção, isso aqui raramente será nulo por causa do Filtro JWT, mas é bom prevenir
        if (empresaId == null) {
            throw new RuntimeException("Tenant ID não encontrado no contexto.");
        }

        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada para o Tenant ID: " + empresaId));

        Cliente cliente = clienteRepository.findById(pedidoRequestDTO.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        // Validar se cliente pertence à mesma empresa
        if (!cliente.getEmpresa().getId().equals(empresaId)) {
            throw new RuntimeException("Cliente não pertence à empresa logada.");
        }

        Pedido pedido = toEntity(pedidoRequestDTO, cliente, empresa);

        // O service.salvar deve conter a lógica transactional (Estoque + Financeiro)
        Pedido pedidoSalvo = service.salvar(pedido);

        return toResponseDTO(pedidoSalvo);
    }

    @Operation(summary = "Cancelar/Deletar Pedido", description = "Remove o pedido e estorna o estoque (se implementado no service).")
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
        pedido.setEmpresa(empresa);

        List<PedidoItem> itens = dto.getItens().stream()
                .map(itemDTO -> {
                    Produto produto = produtoRepository.findById(itemDTO.getProdutoId())
                            .orElseThrow(() -> new RuntimeException("Produto não encontrado com ID: " + itemDTO.getProdutoId()));

                    // Segurança: Produto pertence à empresa?
                    if (!produto.getEmpresa().getId().equals(empresa.getId())) {
                        throw new RuntimeException("Produto " + produto.getNome() + " não pertence à empresa logada.");
                    }

                    // Aviso de divergência de preço (Regra de Negócio)
                    if (itemDTO.getPrecoUnitario().compareTo(produto.getPreco()) != 0) {
                        logger.warn("Divergência de preço no produto {}: DTO={}, Cadastro={}",
                                produto.getNome(), itemDTO.getPrecoUnitario(), produto.getPreco());
                    }

                    PedidoItem item = new PedidoItem();
                    item.setProduto(produto);
                    item.setQuantidade(itemDTO.getQuantidade());
                    item.setPreco(itemDTO.getPrecoUnitario());
                    item.setPedido(pedido);
                    return item;
                })
                .collect(Collectors.toList());

        pedido.setItens(itens);
        return pedido;
    }
}
