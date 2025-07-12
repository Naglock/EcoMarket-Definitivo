package cl.ecomarket.api.assemblers;

import cl.ecomarket.api.controller.ProductoControllerV2;
import cl.ecomarket.api.model.Producto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class ProductoModelAssembler implements RepresentationModelAssembler<Producto, EntityModel<Producto>> {

    @Override
    public EntityModel<Producto> toModel(Producto producto) {
        return EntityModel.of(producto,
            linkTo(methodOn(ProductoControllerV2.class).obtenerPorProductoId(producto.getId()))
                .withSelfRel()
                .withType("GET"),
            linkTo(methodOn(ProductoControllerV2.class).listarTodos())
                .withRel("todos-los-productos")
                .withType("GET"),
            linkTo(methodOn(ProductoControllerV2.class).eliminarProducto(producto.getId()))
                .withRel("eliminar")
                .withType("DELETE"),
            linkTo(methodOn(ProductoControllerV2.class).actualizarPrecio(producto.getId(), producto.getPrecio()))
                .withRel("actualizar-precio")
                .withType("PATCH")
        );
    }
}
