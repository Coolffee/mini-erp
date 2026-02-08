package br.com.mini_erp.cadastro;

import br.com.mini_erp.shared.exception.ResourceNotFoundException; // Importe ResourceNotFoundException
import br.com.mini_erp.shared.exception.BusinessException; // Importe BusinessException
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import br.com.mini_erp.shared.JwtUtil;
import org.springframework.security.core.userdetails.UsernameNotFoundException; // Importe essa exceção se for usá-la aqui

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UsuarioController(UsuarioRepository repository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String email, @RequestParam String senha) {
        Usuario usuario = repository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com e-mail: " + email)); // <<-- ALTERADO AQUI

        if (!passwordEncoder.matches(senha, usuario.getSenha())) {
            // Em um sistema real, para evitar enumeração de usuários,
            // é comum retornar uma mensagem mais genérica como "Credenciais inválidas"
            // para ambos os casos de usuário não encontrado ou senha incorreta.
            // Aqui, para demonstração, mantemos o 401 para senha incorreta explicitamente.
            throw new BusinessException("Senha incorreta"); // <<-- ALTERADO AQUI
            // return ResponseEntity.status(401).body("Senha incorreta"); // Outra opção
        }
        String token = jwtUtil.gerarToken(email, usuario.getEmpresa().getId());
        return ResponseEntity.ok(token);
    }
}