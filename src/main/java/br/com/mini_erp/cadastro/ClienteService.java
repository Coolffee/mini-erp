package br.com.mini_erp.cadastro;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importe Transactional
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

    @Transactional // Adicionado para garantir a transação na persistência
    public Cliente salvar(Cliente cliente) {
        // Antes de salvar, pode-se adicionar validações ou lógicas de negócio adicionais
        return repository.save(cliente);
    }

    public Cliente buscarPorId(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new RuntimeException("Cliente não encontrado")
        );
    }

    @Transactional
    public void deletar(Long id) {
        // Antes de deletar, você pode verificar dependências ou status
        repository.deleteById(id);
    }

    @Transactional
    public Cliente atualizar(Long id, Cliente clienteAtualizado) {
        Cliente clienteExistente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        // O ID da empresa do cliente existente NÃO DEVE ser alterado aqui.
        // O multi-tenancy garante que você só acessa clientes da sua empresa.
        // Se a empresa for setada no DTO de Request, ela deve ser ignorada ou validada.
        // Para simplificar, estamos apenas copiando campos que o RequestDTO enviaria.
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



