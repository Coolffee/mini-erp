package br.com.mini_erp.bi;

import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/bi")
public class BiController {

    private final BiService service;

    public BiController(BiService service) {
        this.service = service;
    }

    @GetMapping("/clientes/total")
    public long totalClientes() {
        return service.totalClientes();
    }

    @GetMapping("/estoque/total")
    public Integer totalEstoque() {
        return service.totalProdutosEmEstoque();
    }

    @GetMapping("/vendas/total")
    public BigDecimal totalVendido() {
        return service.totalVendido();
    }

    @GetMapping("/clientes/{id}/pedidos")
    public long pedidosPorCliente(@PathVariable Long id) {
        return service.pedidosPorCliente(id);
    }
}
