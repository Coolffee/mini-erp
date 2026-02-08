package br.com.mini_erp.financeiro;

import br.com.mini_erp.shared.exception.ResourceNotFoundException; // Importe
import br.com.mini_erp.vendas.Pedido;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class FaturaService {

    private final FaturaRepository repository;

    public FaturaService(FaturaRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Fatura gerarFatura(Pedido pedido) {
        BigDecimal total = pedido.getItens().stream()
                .map(item -> item.getPreco().multiply(
                        BigDecimal.valueOf(item.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Fatura fatura = new Fatura(pedido, total);
        return repository.save(fatura);
    }

    @Transactional
    public Fatura pagar(Long id) {
        Fatura fatura = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fatura não encontrada com ID: " + id)); // <<-- ALTERADO AQUI

        if (fatura.getStatus() == StatusFatura.PAGA) {
            // Opcional: lançar BusinessException se já estiver paga
            // throw new BusinessException("Fatura com ID: " + id + " já está paga.");
        }

        fatura.setStatus(StatusFatura.PAGA);
        return repository.save(fatura);
    }
}
