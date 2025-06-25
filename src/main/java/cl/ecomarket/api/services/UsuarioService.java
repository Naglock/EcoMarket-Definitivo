package cl.ecomarket.api.services;

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

    public Usuario encontrarPorId(Long id) throws Exception {
        if (!usuarioRepository.existsById(id)) {
            throw new Exception("Usuario no encontrado");
        }
        return usuarioRepository.findById(id).get();
    }

    public Usuario guardarUsuario(Usuario usuario) throws Exception {
        if (usuario.getPnombre() == null || usuario.getPnombre().isEmpty()) {
            throw new Exception("El primer nombre del usuario no puede estar vacío");
        }
        if (usuario.getAppaterno() == null || usuario.getAppaterno().isEmpty()) {
            throw new Exception("El apellido paterno del usuario no puede estar vacío");
        }
        if (usuario.getDireccion() == null || usuario.getDireccion().isEmpty()) {
            throw new Exception("La dirección del usuario no puede estar vacía");
        }
        return usuarioRepository.save(usuario);
    }
    
    // Borra al usuario por ID
    public void borrarUsuario(Long id) throws Exception {
        if (!usuarioRepository.existsById(id)) {    
            throw new Exception("Usuario no encontrado");
        }
        usuarioRepository.deleteById(id);
    }

}
