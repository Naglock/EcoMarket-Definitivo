package cl.ecomarket.api.assemblers;

import cl.ecomarket.api.controller.VentaControllerV2;
import cl.ecomarket.api.model.Venta;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.stereotype.Component;

@Component
public class VentaModelAssembler implements RepresentationModelAssembler<Venta, EntityModel<Venta>> {

    @Override
    public EntityModel<Venta> toModel(Venta venta) {
        return EntityModel.of(venta,
            linkTo(methodOn(VentaControllerV2.class).obtenerPorId(venta.getId()))
                .withSelfRel()
                .withType("GET"),
            linkTo(methodOn(VentaControllerV2.class).obtenerTodas())
                .withRel("todas-las-ventas")
                .withType("GET"),
            linkTo(methodOn(VentaControllerV2.class).eliminarVenta(venta.getId()))
                .withRel("eliminar")
                .withType("DELETE"),
            linkTo(methodOn(VentaControllerV2.class).generarFactura(venta.getId()))
                .withRel("factura")
                .withType("GET")
        );
    }
}
