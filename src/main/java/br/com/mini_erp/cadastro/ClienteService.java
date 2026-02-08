package br.com.mini_erp.cadastro;

import br.com.mini_erp.shared.TenantContext; // Importe TenantContext
import br.com.mini_erp.shared.exception.BusinessException; // Importe BusinessException
import br.com.mini_erp.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository repository;

    public ClienteService(ClienteRepository repository) {
        this.repository = repository;
    }

    public List<Cliente> listarTodos() {
        return repository.findAll();
    }

    public Cliente buscarPorId(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Cliente não encontrado com ID: " + id)
        );
    }

    @Transactional
    public Cliente salvar(Cliente cliente) {
        Long empresaId = TenantContext.getCurrentTenantId();
        if (empresaId == null) {
            throw new BusinessException("ID da empresa não disponível no contexto.");
        }

        // Validação de unicidade para Documento e Email
        validarUnicidadeCliente(cliente.getDocumento(), cliente.getEmail(), empresaId, null); // null para novo cliente

        return repository.save(cliente);
    }

    @Transactional
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Cliente não encontrado com ID: " + id);
        }
        repository.deleteById(id);
    }

    @Transactional
    public Cliente atualizar(Long id, Cliente clienteAtualizado) {
        Cliente clienteExistente = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com ID: " + id));

        Long empresaId = TenantContext.getCurrentTenantId();
        if (empresaId == null) {
            throw new BusinessException("ID da empresa não disponível no contexto.");
        }

        // Validação de unicidade para Documento e Email (excluindo o próprio cliente)
        validarUnicidadeCliente(clienteAtualizado.getDocumento(), clienteAtualizado.getEmail(), empresaId, id);

        clienteExistente.setNome(clienteAtualizado.getNome());
        clienteExistente.setEmail(clienteAtualizado.getEmail());
        clienteExistente.setDataNascimento(clienteAtualizado.getDataNascimento());
        clienteExistente.setDocumento(clienteAtualizado.getDocumento());
        // A empresa_id não deve ser alterada em uma atualização de cliente.
        // O clienteExistente já tem a empresa_id correta.

        return repository.save(clienteExistente);
    }

    public List<Cliente> buscarComFiltros(String nome, String email) {
        // O TenantFilterAspect garante que a busca já é feita dentro da empresa correta
        if (nome != null && !nome.isEmpty()) {
            return repository.findByNomeContainingIgnoreCase(nome);
        } else if (email != null && !email.isEmpty()) {
            // Note que o findByEmailContainingIgnoreCase pode não ser ideal para emails
            // se o objetivo for uma busca exata ou por unicidade.
            // Para unicidade, usamos os novos métodos específicos.
            return repository.findByEmailContainingIgnoreCase(email);
        } else {
            return repository.findAll();
        }
    }

    // --- NOVO METODO PRIVADO PARA CENTRALIZAR A VALIDAÇÃO DE UNICIDADE ---
    private void validarUnicidadeCliente(String documento, String email, Long empresaId, Long clienteIdAtual) {
        // Valida documento
        repository.findByDocumentoAndEmpresaId(documento, empresaId).ifPresent(c -> {
            if (clienteIdAtual == null || !c.getId().equals(clienteIdAtual)) { // Se é um novo cliente ou documento já pertence a outro cliente
                throw new BusinessException("Documento '" + documento + "' já cadastrado para outra empresa ou para outro cliente na sua empresa.");
            }
        });

        // Valida email
        repository.findByEmailAndEmpresaId(email, empresaId).ifPresent(c -> {
            if (clienteIdAtual == null || !c.getId().equals(clienteIdAtual)) { // Se é um novo cliente ou email já pertence a outro cliente
                throw new BusinessException("E-mail '" + email + "' já cadastrado para outra empresa ou para outro cliente na sua empresa.");
            }
        });
    }
}

