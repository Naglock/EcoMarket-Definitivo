package cl.ecomarket.api.assemblers;

import cl.ecomarket.api.controller.UsuarioController;
import cl.ecomarket.api.model.Usuario;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.stereotype.Component;

@Component
public class UsuarioModelAssembler implements RepresentationModelAssembler<Usuario, EntityModel<Usuario>> {

    @Override
    public EntityModel<Usuario> toModel(Usuario usuario) {
        return EntityModel.of(usuario,
            linkTo(methodOn(UsuarioController.class).buscar(usuario.getId()))
                .withSelfRel()
                .withType("GET"),
            linkTo(methodOn(UsuarioController.class).listar())
                .withRel("todos-los-usuarios")
                .withType("GET"),
            linkTo(methodOn(UsuarioController.class).actualizar(usuario.getId(), usuario))
                .withRel("actualizar")
                .withType("PUT"),
            linkTo(methodOn(UsuarioController.class).eliminar(usuario.getId()))
                .withRel("eliminar")
                .withType("DELETE")
        );
    }
}
