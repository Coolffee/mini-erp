package br.com.mini_erp.financeiro;

import br.com.mini_erp.shared.exception.ResourceNotFoundException;
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

    @Transactional // Garante que a geração da fatura seja parte de uma transação
    public Fatura gerarFatura(Pedido pedido) {
        // Verifica se já existe uma fatura para este pedido (evitar duplicação)
        // Opcional: Você pode adicionar uma busca aqui por pedidoId se for um requisito
        // para garantir que um pedido só tenha uma fatura.

        BigDecimal total = pedido.getItens().stream()
                .map(item -> item.getPreco().multiply(
                        BigDecimal.valueOf(item.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Fatura fatura = new Fatura(pedido, total);
        // A empresa da fatura será a mesma do pedido, que já foi definida pelo TenantContext
        return repository.save(fatura);
    }

    @Transactional
    public Fatura pagar(Long id) {
        Fatura fatura = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fatura não encontrada com ID: " + id));

        if (fatura.getStatus() == StatusFatura.PAGA) {
            // throw new BusinessException("Fatura com ID: " + id + " já está paga.");
        }

        fatura.setStatus(StatusFatura.PAGA);
        return repository.save(fatura);
    }
}