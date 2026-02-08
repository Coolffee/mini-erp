package br.com.mini_erp.config;

import br.com.mini_erp.cadastro.Usuario;
import br.com.mini_erp.cadastro.UsuarioRepository;
import org.springframework.security.core.userdetails.User; // Importa User do Spring Security
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.ArrayList; // Para as authorities (roles), por enquanto vazias

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UserDetailsServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com email: " + email));

        // Aqui você pode adicionar as roles/autoridades do usuário se tivesse no seu modelo
        // Por enquanto, vamos retornar uma lista vazia de authorities.
        return new User(usuario.getEmail(), usuario.getSenha(), new ArrayList<>());
    }
}