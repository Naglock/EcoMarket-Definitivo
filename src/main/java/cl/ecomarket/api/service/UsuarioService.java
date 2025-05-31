package cl.ecomarket.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cl.ecomarket.api.model.Usuario;
import cl.ecomarket.api.repository.UsuarioRepository;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    public Usuario encontrarPorId(Long id) {
        return usuarioRepository.findById(id).get();
    }

    public Usuario guardarUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }
    // Borra al usuario por ID
    public void borrarUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }

}
