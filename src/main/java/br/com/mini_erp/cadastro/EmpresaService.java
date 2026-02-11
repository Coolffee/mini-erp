package br.com.mini_erp.cadastro;

import br.com.mini_erp.cadastro.dto.EmpresaSetupDTO;
import br.com.mini_erp.shared.exception.BusinessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public EmpresaService(EmpresaRepository empresaRepository,
                          UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder) {
        this.empresaRepository = empresaRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional // Garante que ou salva tudo, ou não salva nada (Atomicidade)
    public Empresa setupEmpresa(EmpresaSetupDTO dto) {
        // 1. Verifica se já existe usuário com esse email (globalmente ou na lógica de negócio)
        if (usuarioRepository.findByEmail(dto.emailAdmin()).isPresent()) {
            throw new BusinessException("Já existe um usuário cadastrado com este e-mail.");
        }

        // 2. Cria e Salva a Empresa
        Empresa empresa = new Empresa();
        empresa.setNome(dto.nomeEmpresa());
        empresa = empresaRepository.save(empresa);

        // 3. Cria e Salva o Usuário Admin vinculado à Empresa
        Usuario admin = new Usuario();
        admin.setEmail(dto.emailAdmin());
        admin.setSenha(passwordEncoder.encode(dto.senhaAdmin())); // Hash da senha
        admin.setRole("ROLE_ADMIN"); // Força ser Admin
        admin.setEmpresa(empresa);

        usuarioRepository.save(admin);

        return empresa;
    }
}