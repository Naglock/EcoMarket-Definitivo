package cl.ecomarket.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.ecomarket.api.model.Usuario;
import cl.ecomarket.api.service.UsuarioService;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<Usuario>> listar() {
        List<Usuario> usuarios = usuarioService.listarUsuarios();
        if (usuarios.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(usuarios);
    }

    @PostMapping
    public ResponseEntity<Usuario> guardar(@RequestBody Usuario usuario) {
        Usuario usuarioNuevo = usuarioService.guardarUsuario(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioNuevo);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscar(@PathVariable Long id) {
        try {
            Usuario usuario = usuarioService.encontrarPorId(id);
            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizar(@PathVariable Long id,@RequestBody Usuario usuario) {
        try {
            Usuario user = usuarioService.encontrarPorId(id);
            user.setId(id);
            user.setPnombre(usuario.getPnombre());
            if (usuario.getSnombre() != null){
                user.setSnombre(usuario.getSnombre());
            }
            user.setAppaterno(usuario.getAppaterno());
            if (usuario.getApmaterno() != null) {
                user.setApmaterno(usuario.getApmaterno());
            }            
            if (usuario.getCorreo() != null) {
                user.setCorreo(usuario.getCorreo());
            }
            user.setDireccion(usuario.getDireccion());
            usuarioService.guardarUsuario(user);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            usuarioService.borrarUsuario(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
