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

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService service;

    public PedidoController(PedidoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Pedido> listarTodos() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public Pedido buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Pedido criar(@Valid @RequestBody Pedido pedido) {
        return service.salvar(pedido);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}
