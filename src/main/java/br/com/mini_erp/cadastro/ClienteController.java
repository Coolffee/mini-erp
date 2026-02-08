package br.com.mini_erp.cadastro;

import br.com.mini_erp.cadastro.dto.ClienteRequestDTO;
import br.com.mini_erp.cadastro.dto.ClienteResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity; // Importe ResponseEntity
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService service;
    private final EmpresaRepository empresaRepository; // Precisaremos para associar o cliente à empresa

    // Adicione o EmpresaRepository no construtor
    public ClienteController(ClienteService service, EmpresaRepository empresaRepository) {
        this.service = service;
        this.empresaRepository = empresaRepository;
    }

    @GetMapping
    public List<ClienteResponseDTO> listarTodos(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String email) {

        return service.buscarComFiltros(nome, email).stream()
                .map(this::toResponseDTO) // Mapeia cada Cliente para ClienteResponseDTO
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> buscarPorId(@PathVariable Long id) {
        Cliente cliente = service.buscarPorId(id);
        return ResponseEntity.ok(toResponseDTO(cliente));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClienteResponseDTO criar(@Valid @RequestBody ClienteRequestDTO clienteRequestDTO) {
        // Obtenha o ID da empresa do contexto (usando TenantContext)
        // Para este exemplo, vou simular o id da empresa, mas ele virá do JWT
        Long empresaId = br.com.mini_erp.shared.TenantContext.getCurrentTenantId(); // <--- Usando o TenantContext

        // Opcional: buscar a empresa para ter certeza que existe
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada para o Tenant ID: " + empresaId));

        Cliente cliente = toEntity(clienteRequestDTO, empresa); // Converte DTO para Entidade
        Cliente clienteSalvo = service.salvar(cliente);
        return toResponseDTO(clienteSalvo);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }

    @PutMapping("/{id}")
    public ClienteResponseDTO atualizar(@PathVariable Long id, @Valid @RequestBody ClienteRequestDTO clienteRequestDTO) {
        // O ID da empresa será gerenciado pelo serviço para garantir que não mude
        Cliente clienteAtualizado = service.atualizar(id, toEntity(clienteRequestDTO, null)); // Empresa será ignorada aqui no toEntity, a lógica de manter a empresa estará no Service
        return toResponseDTO(clienteAtualizado);
    }

    // --- Métodos de Conversão (Mapper) ---

    private ClienteResponseDTO toResponseDTO(Cliente cliente) {
        return new ClienteResponseDTO(
                cliente.getId(),
                cliente.getNome(),
                cliente.getEmail(),
                cliente.getDataNascimento(),
                cliente.getDocumento(),
                cliente.getEmpresa() != null ? cliente.getEmpresa().getId() : null,
                cliente.getEmpresa() != null ? cliente.getEmpresa().getNome() : null
        );
    }

    private Cliente toEntity(ClienteRequestDTO dto, Empresa empresa) {
        // Note que o ID do Cliente não vem do DTO de Request, pois é gerado pelo banco
        Cliente cliente = new Cliente(
                dto.getNome(),
                dto.getEmail(),
                dto.getDataNascimento(),
                dto.getDocumento(),
                empresa // A empresa é passada separadamente, do TenantContext
        );
        return cliente;
    }
}

