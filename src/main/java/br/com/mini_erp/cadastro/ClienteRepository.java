package br.com.mini_erp.cadastro;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional; // Importar Optional

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    List<Cliente> findByNomeContainingIgnoreCase(String nome);
    List<Cliente> findByEmailContainingIgnoreCase(String email); // Pode ser removido se o buscarComFiltros for alterado para usar o filtro do Hibernate

    // --- NOVOS MÉTODOS PARA VALIDAÇÃO DE UNICIDADE ---
    // Encontra cliente pelo documento DENTRO de uma empresa específica
    Optional<Cliente> findByDocumentoAndEmpresaId(String documento, Long empresaId);

    // Encontra cliente pelo e-mail DENTRO de uma empresa específica
    Optional<Cliente> findByEmailAndEmpresaId(String email, Long empresaId);

    // Encontra cliente pelo documento DENTRO de uma empresa específica, excluindo o ID do cliente atual
    Optional<Cliente> findByDocumentoAndEmpresaIdAndIdIsNot(String documento, Long empresaId, Long id);

    // Encontra cliente pelo e-mail DENTRO de uma empresa específica, excluindo o ID do cliente atual
    Optional<Cliente> findByEmailAndEmpresaIdAndIdIsNot(String email, Long empresaId, Long id);
    // --------------------------------------------------
}