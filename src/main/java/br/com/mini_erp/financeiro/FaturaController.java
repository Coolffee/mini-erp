package br.com.mini_erp.financeiro;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/faturas")
public class FaturaController {

    private final FaturaService service;

    public FaturaController(FaturaService service) {
        this.service = service;
    }

    @PutMapping("/{id}/pagar")
    public Fatura pagar(@PathVariable Long id) {
        return service.pagar(id);
    }
}
