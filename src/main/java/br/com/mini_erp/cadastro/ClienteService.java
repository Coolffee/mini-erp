package br.com.mini_erp.cadastro;

import br.com.mini_erp.shared.exception.ResourceNotFoundException; // Importe
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

    @Transactional
    public Cliente salvar(Cliente cliente) {
        return repository.save(cliente);
    }

    public Cliente buscarPorId(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Cliente não encontrado com ID: " + id) // <<-- ALTERADO AQUI
        );
    }

    @Transactional
    public void deletar(Long id) {
        // Opcional: Verificar se existe antes de deletar
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Cliente não encontrado com ID: " + id);
        }
        repository.deleteById(id);
    }

    @Transactional
    public Cliente atualizar(Long id, Cliente clienteAtualizado) {
        Cliente clienteExistente = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com ID: " + id)); // <<-- ALTERADO AQUI

        clienteExistente.setNome(clienteAtualizado.getNome());
        clienteExistente.setEmail(clienteAtualizado.getEmail());
        clienteExistente.setDataNascimento(clienteAtualizado.getDataNascimento());
        clienteExistente.setDocumento(clienteAtualizado.getDocumento());

        return repository.save(clienteExistente);
    }

    public List<Cliente> buscarComFiltros(String nome, String email) {
        if (nome != null && !nome.isEmpty()) {
            return repository.findByNomeContainingIgnoreCase(nome);
        } else if (email != null && !email.isEmpty()) {
            return repository.findByEmailContainingIgnoreCase(email);
        } else {
            return repository.findAll();
        }
    }
}



