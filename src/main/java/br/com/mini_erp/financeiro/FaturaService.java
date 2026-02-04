package br.com.mini_erp.financeiro;

import br.com.mini_erp.vendas.Pedido;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class FaturaService {

    private final FaturaRepository repository;

    public FaturaService(FaturaRepository repository) {
        this.repository = repository;
    }

    public Fatura gerarFatura(Pedido pedido) {
        BigDecimal total = pedido.getItens().stream()
                .map(item -> item.getPreco().multiply(
                        BigDecimal.valueOf(item.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Fatura fatura = new Fatura(pedido, total);
        return repository.save(fatura);
    }

    public Fatura pagar(Long id) {
        Fatura fatura = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fatura não encontrada"));

        fatura.setStatus(StatusFatura.PAGA);
        return repository.save(fatura);
    }
}
