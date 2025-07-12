package cl.ecomarket.api.controller;

import cl.ecomarket.api.assemblers.UsuarioModelAssembler;
import cl.ecomarket.api.model.Usuario;
import cl.ecomarket.api.services.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v2/usuarios")
@Tag(name = "Usuarios V2", description = "Operaciones con enlaces HATEOAS para los usuarios")
public class UsuarioControllerV2 {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioModelAssembler assembler;

    @GetMapping
    @Operation(summary = "Listar todos los usuarios", description = "Obtiene una lista de todos los usuarios con enlaces HATEOAS")
    public ResponseEntity<CollectionModel<EntityModel<Usuario>>> listar() {
        List<Usuario> usuarios = usuarioService.listarUsuarios();

        if (usuarios.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<EntityModel<Usuario>> modelos = usuarios.stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                CollectionModel.of(modelos)
        );
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo usuario", description = "Crea un nuevo usuario en el sistema y devuelve su representación con enlaces HATEOAS")
    public ResponseEntity<EntityModel<Usuario>> guardar(@RequestBody Usuario usuario) {
        try {
            Usuario nuevoUsuario = usuarioService.guardarUsuario(usuario);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(assembler.toModel(nuevoUsuario));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID", description = "Obtiene un usuario específico por su ID y devuelve su representación con enlaces HATEOAS")
    public ResponseEntity<EntityModel<Usuario>> buscar(@PathVariable Long id) {
        try {
            Usuario usuario = usuarioService.encontrarPorId(id);
            return ResponseEntity.ok(assembler.toModel(usuario));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar usuario", description = "Actualiza un usuario existente por su ID y devuelve su representación con enlaces HATEOAS")
    public ResponseEntity<EntityModel<Usuario>> actualizar(@PathVariable Long id, @RequestBody Usuario usuario) {
        try {
            Usuario existente = usuarioService.encontrarPorId(id);

            if (usuario.getSnombre() != null) {
                existente.setSnombre(usuario.getSnombre());
            }
            if (usuario.getCorreo() != null) {
                existente.setCorreo(usuario.getCorreo());
            }
            if (usuario.getApmaterno() != null) {
                existente.setApmaterno(usuario.getApmaterno());
            }

            existente.setPnombre(usuario.getPnombre());
            existente.setAppaterno(usuario.getAppaterno());
            existente.setDireccion(usuario.getDireccion());

            Usuario actualizado = usuarioService.guardarUsuario(existente);
            return ResponseEntity.ok(assembler.toModel(actualizado));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar usuario por ID")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        try {
            usuarioService.borrarUsuario(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
