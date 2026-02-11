package br.com.mini_erp.cadastro;
//padrão
import br.com.mini_erp.shared.JwtUtil;
import br.com.mini_erp.shared.TenantContext;
import br.com.mini_erp.shared.exception.BusinessException;
import br.com.mini_erp.shared.exception.ResourceNotFoundException;
//jakarta
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
//springframework
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

// Imports do Swagger
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "1. Gestão de Acesso", description = "Endpoints para autenticação e gestão de usuários")
public class UsuarioController {

    private final UsuarioRepository repository;
    private final EmpresaRepository empresaRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UsuarioController(UsuarioRepository repository,
                             EmpresaRepository empresaRepository,
                             PasswordEncoder passwordEncoder,
                             JwtUtil jwtUtil) {
        this.repository = repository;
        this.empresaRepository = empresaRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // Metodo de login com swagger
    @Operation(summary = "Autenticação de Usuário", description = "Recebe email e senha para retornar um Token JWT.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso. Token retornado."),
            @ApiResponse(responseCode = "400", description = "Senha incorreta ou dados inválidos."), // BusinessException costuma retornar 400
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado.")
    })
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String email, @RequestParam String senha) {
        Usuario usuario = repository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com e-mail: " + email));

        if (!passwordEncoder.matches(senha, usuario.getSenha())) {
            throw new BusinessException("Senha incorreta");
        }

        // Gera o token com o ID da empresa do usuário
        String token = jwtUtil.gerarToken(email, usuario.getEmpresa().getId());
        return ResponseEntity.ok(token);
    }

    // Metodo com usuario com swagger
    @Operation(summary = "Criar Novo Usuário", description = "Cria um usuário vinculado à empresa do token atual (Requer role ADMIN).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso."),
            @ApiResponse(responseCode = "403", description = "Acesso negado (apenas ADMIN pode criar)."),
            @ApiResponse(responseCode = "400", description = "E-mail já cadastrado.")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public Usuario criarUsuario(@RequestBody @Valid UsuarioDTO dto) {
        Long empresaId = TenantContext.getCurrentTenantId();

        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada"));

        if (repository.findByEmail(dto.email()).isPresent()) {
            throw new BusinessException("E-mail já cadastrado.");
        }

        Usuario usuario = new Usuario();
        usuario.setEmail(dto.email());
        usuario.setSenha(passwordEncoder.encode(dto.senha()));
        usuario.setRole(dto.role());
        usuario.setEmpresa(empresa);

        return repository.save(usuario);
    }
}

// DTO interno
record UsuarioDTO(
        @NotBlank String email,
        @NotBlank String senha,
        @NotBlank String role
) {}