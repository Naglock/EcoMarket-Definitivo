package cl.ecomarket.api.assemblers;

import cl.ecomarket.api.controller.TiendaControllerV2;
import cl.ecomarket.api.model.Tienda;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class TiendaModelAssembler implements RepresentationModelAssembler<Tienda, EntityModel<Tienda>> {

    @Override
    public EntityModel<Tienda> toModel(Tienda tienda) {
        return EntityModel.of(tienda,
            linkTo(methodOn(TiendaControllerV2.class).obtenerTiendaPorId(tienda.getId()))
                .withSelfRel()
                .withType("GET"),
            linkTo(methodOn(TiendaControllerV2.class).obtenerTodasLasTiendas())
                .withRel("todas-las-tiendas")
                .withType("GET"),
            linkTo(methodOn(TiendaControllerV2.class).eliminarTienda(tienda.getId()))
                .withRel("eliminar")
                .withType("DELETE"),
            linkTo(methodOn(TiendaControllerV2.class).crearTienda(null))
                .withRel("crear")
                .withType("POST")
        );
    }
}
