package cl.ecomarket.api.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.ecomarket.api.assemblers.PedidoModelAssembler;
import cl.ecomarket.api.model.Estados;
import cl.ecomarket.api.model.Pedido;
import cl.ecomarket.api.services.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/v2/pedidos")
@Tag(name = "Pedidos V2", description = "Operaciones relacionadas con los pedidos con HATEOAS")
public class PedidoControllerV2 {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private PedidoModelAssembler assembler;

    @GetMapping
    @Operation(summary = "Listar todos los pedidos", description = "Obtiene una lista de todos los pedidos registrados con links HATEOAS")
    public ResponseEntity<CollectionModel<EntityModel<Pedido>>> listarTodo() {
        List<Pedido> pedidos = pedidoService.obtenerTodos();
        if (pedidos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        List<EntityModel<Pedido>> pedidosModel = pedidos.stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        return ResponseEntity.ok(CollectionModel.of(pedidosModel,
                linkTo(methodOn(PedidoControllerV2.class).listarTodo()).withSelfRel()));
    }

    @GetMapping("/{idPedido}")
    @Operation(summary = "Obtener pedido por ID", description = "Retorna un pedido específico por su ID con link HATEOAS")
    public ResponseEntity<EntityModel<Pedido>> obtenerPorId(@PathVariable Long idPedido) {
        try {
            Pedido pedido = pedidoService.encontrarPorId(idPedido);
            return ResponseEntity.ok(assembler.toModel(pedido));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/cliente/{idCliente}")
    @Operation(summary = "Obtener pedidos por ID de cliente", description = "Retorna una lista de pedidos asociados a un cliente específico con links HATEOAS")
    public ResponseEntity<CollectionModel<EntityModel<Pedido>>> obtenerPorIdCliente(@PathVariable Long idCliente) {
        try {
            List<Pedido> pedidos = pedidoService.obtenerPorClienteId(idCliente);
            if (pedidos.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            List<EntityModel<Pedido>> pedidosModel = pedidos.stream()
                    .map(assembler::toModel)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(CollectionModel.of(pedidosModel));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo pedido", description = "Crea un nuevo pedido en el sistema y devuelve su representación con enlaces HATEOAS")
    public ResponseEntity<EntityModel<Pedido>> guardarPedido(@RequestBody Pedido pedido) {
        try {
            Pedido nuevoPedido = pedidoService.crearPedido(pedido);
            return ResponseEntity.created(
                    linkTo(methodOn(PedidoControllerV2.class).obtenerPorId(nuevoPedido.getId())).toUri())
                    .body(assembler.toModel(nuevoPedido));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/cambiar-estado/{idPedido}")
    @Operation(summary = "Cambiar estado del pedido", description = "Permite a Logística cambiar el estado de un pedido con enlaces HATEOAS")
    public ResponseEntity<EntityModel<Pedido>> cambiarEstado(@PathVariable Long idPedido, @RequestParam Estados estado) {
        try {
            Pedido pedido = pedidoService.encontrarPorId(idPedido);
            if (pedido.getEstado() == Estados.RECHAZADA || pedido.getEstado() == null) {
                return ResponseEntity.badRequest().build();
            }
            pedido.setEstado(estado);
            pedidoService.crearPedido(pedido);
            return ResponseEntity.ok(assembler.toModel(pedido));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

}
