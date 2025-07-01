package cl.ecomarket.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import cl.ecomarket.api.model.Estados;
import cl.ecomarket.api.model.Pedido;
import cl.ecomarket.api.services.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/pedidos")
@Tag(name = "Pedidos", description = "Operaciones relacionadas con los pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @GetMapping
    @Operation(summary = "Listar todos los pedidos", description = "Obtiene una lista de todos los pedidos registrados")
    public ResponseEntity<List<Pedido>> listarTodo(){
        List<Pedido> pedidos = pedidoService.obtenerTodos();
        if (pedidos.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/{idPedido}")
    @Operation(summary = "Obtener pedido por ID", description = "Retorna un pedido específico por su ID")
    public ResponseEntity<Pedido> obtenerPorId(@PathVariable Long idPedido) {
        try {
            Pedido pedido = pedidoService.encontrarPorId(idPedido);
            return ResponseEntity.ok(pedido);
        } catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/cliente/{idCliente}")
    @Operation(summary = "Obtener pedidos por ID de cliente", description = "Retorna una lista de pedidos asociados a un cliente específico")
    public ResponseEntity<List<Pedido>> obtenerPorIdCliente(@PathVariable Long idCliente){
        try {
            List<Pedido> pedidos = pedidoService.obtenerPorClienteId(idCliente);
            return ResponseEntity.ok(pedidos);
        } catch (Exception e) {
            return ResponseEntity.notFound().build(); // Si no encuentra pedidos, retorna notFound
        }
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo pedido", description = "Crea un nuevo pedido en el sistema")
    public ResponseEntity<Pedido> guardarPedido(@RequestBody Pedido pedido) {
        try {
            Pedido nuevoPedido = pedidoService.crearPedido(pedido);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoPedido); // Retorna un estado CREATED con el pedido creado
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // Si hay un error, retorna un estado BAD_REQUEST
        }
    }


    @PatchMapping("/cambiar-estado/{idPedido}")
    @Operation(summary = "Cambiar estado del pedido", description = "Permite a Logística cambiar el estado de un pedido")
    public ResponseEntity<Pedido> cambiarEstado(@PathVariable Long idPedido, @RequestParam Estados estado) { // Funcion para Logistica. Permite Cambiar el estado del pedido
        try {
            Pedido pedido = pedidoService.encontrarPorId(idPedido);
            if (pedido.getEstado() == Estados.RECHAZADA || pedido.getEstado() == null){ // Si esta RECHAZADA, logistica no puede generar ningun cambio en el estado
                return ResponseEntity.badRequest().build();                             // Si esta null, significa que aun no se ha aprobado por ventas
            }
            pedido.setEstado(estado);
            pedidoService.crearPedido(pedido);
            return ResponseEntity.ok(pedido);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();  // notFound si no encuentra al pedido por el Id
        }
    }

}
