package cl.ecomarket.api.assemblers;

import cl.ecomarket.api.controller.PedidoControllerV2;
import cl.ecomarket.api.model.Pedido;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class PedidoModelAssembler implements RepresentationModelAssembler<Pedido, EntityModel<Pedido>> {

    @Override
    public EntityModel<Pedido> toModel(Pedido pedido) {
        return EntityModel.of(pedido,
            linkTo(methodOn(PedidoControllerV2.class).obtenerPorId(pedido.getId()))
                .withSelfRel()
                .withType("GET"),
            linkTo(methodOn(PedidoControllerV2.class).listarTodo())
                .withRel("todos-los-pedidos")
                .withType("GET"),
            linkTo(methodOn(PedidoControllerV2.class).cambiarEstado(pedido.getId(), pedido.getEstado()))
                .withRel("cambiar-estado")
                .withType("PATCH")
        );
    }
}
