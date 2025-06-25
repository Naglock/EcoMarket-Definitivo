package cl.ecomarket.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.ecomarket.api.model.Usuario;
import cl.ecomarket.api.services.UsuarioService;

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
        try {
            Usuario nuevoUsuario = usuarioService.guardarUsuario(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario); // Retorna 201 Created
        } catch (Exception e) {
            return ResponseEntity.badRequest().build(); // Retorna 400 Bad Request si hay un error
        }
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
    public ResponseEntity<Usuario> actualizar(@PathVariable Long id,@RequestBody Usuario usuario) { // Usuario puede tener datos de tipo null.
        try {                                                                                       // En ellos se hace la comparacion != null, si no estan null, se hace un Set.
            Usuario usuarioExistente = usuarioService.encontrarPorId(id);
            if (usuario.getSnombre() != null) {
                usuarioExistente.setSnombre(usuario.getSnombre());
            }
            if (usuario.getCorreo() != null) {
                usuarioExistente.setCorreo(usuario.getCorreo());
            }
            if (usuario.getApmaterno() != null) {
                usuarioExistente.setApmaterno(usuario.getApmaterno());
            }
            usuarioExistente.setPnombre(usuario.getPnombre());
            usuarioExistente.setAppaterno(usuario.getAppaterno());
            usuarioExistente.setDireccion(usuario.getDireccion());
            try {
                Usuario actualizado = usuarioService.guardarUsuario(usuarioExistente);
                return ResponseEntity.ok(actualizado);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // Retorna 400 Bad Request
            }            
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
