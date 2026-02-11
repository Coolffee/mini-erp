package br.com.mini_erp.config;

import br.com.mini_erp.cadastro.Usuario;
import br.com.mini_erp.cadastro.UsuarioRepository;
import org.springframework.security.core.GrantedAuthority; // Importe
import org.springframework.security.core.authority.SimpleGrantedAuthority; // Importe
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections; // Importe
import java.util.List;

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

        // --- ALTERAÇÃO AQUI ---
        // Converte a string "ROLE_ADMIN" (ou "ROLE_USER") do banco para uma Authority
        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority(usuario.getRole())
        );

        return new User(usuario.getEmail(), usuario.getSenha(), authorities);
    }
}

