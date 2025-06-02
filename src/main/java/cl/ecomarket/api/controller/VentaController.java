package cl.ecomarket.api.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.ecomarket.api.model.Estados;
import cl.ecomarket.api.model.Inventario;
import cl.ecomarket.api.model.ItemPedido;
import cl.ecomarket.api.model.Pedido;
import cl.ecomarket.api.model.Venta;
import cl.ecomarket.api.services.InventarioService;
import cl.ecomarket.api.services.PedidoService;
import cl.ecomarket.api.services.TiendaService;
import cl.ecomarket.api.services.VentaService;

@RestController
@RequestMapping("api/v1/ventas")
public class VentaController {

    @Autowired
    private VentaService ventaService;
    @Autowired
    private PedidoService pedidoService;
    @Autowired
    private TiendaService tiendaService;
    @Autowired
    private InventarioService inventarioService;

    @PostMapping("/")
    public ResponseEntity<Venta> registrarVenta(@RequestBody Venta venta,@RequestParam Estados estado) {
        venta.setFecha(LocalDate.now());
        venta.setEstado(estado); // Coloca el estado de la venta como APROBADA/RECHAZADA. Cualquier otra da error.
        venta.setPedido(pedidoService.encontrarPorId(venta.getPedido().getId())); // A partir de "pedido": {"id":Long} se obtiene el pedido que si existe en el repository
        venta.setTienda(tiendaService.obtenerTiendaPorId(venta.getPedido().getTienda().getId())); // y con este mismo pedido se obtiene la tienda del repository
        Double total = 0.0;                                                                       // Hacer errores tipo not Found para ambos set (Pedido y Tienda) (PENDIENTE)
        if (estado == Estados.APROBADA){
            for (ItemPedido item : venta.getPedido().getItems()){
                Double Subtotal = 0.0;
                Subtotal += (item.getCantidad() * item.getProducto().getPrecio());
                Inventario inventario = inventarioService.encontrarPorProductoYTienda(item.getProducto().getId(), venta.getTienda().getId());
                inventario.setStock(inventario.getStock()-item.getCantidad());
                inventarioService.guardarInventario(inventario); //actualiza el stock del inventario.
                total += Subtotal;
            }
            venta.setTotal(total);
            Venta guardada = ventaService.generarVenta(venta);
            Pedido pedido = pedidoService.encontrarPorId(venta.getPedido().getId());
            pedido.setEstado(Estados.EN_PREPARACION);
            pedidoService.crearPedido(pedido); //actualiza el pedido y lo pone de estado EN_PREPARACION
            return ResponseEntity.ok(guardada);
            } else if (estado == Estados.RECHAZADA){
                for (ItemPedido item : venta.getPedido().getItems()){
                    Double Subtotal = 0.0;
                    Subtotal += (item.getCantidad() * item.getProducto().getPrecio());
                    total += Subtotal;
                }
                venta.setTotal(total);
                Pedido pedido = pedidoService.encontrarPorId(venta.getPedido().getId());
                pedido.setEstado(Estados.RECHAZADA);
                pedidoService.crearPedido(pedido); //actualiza el pedido y le pone de estado RECHAZADA
                Venta guardada = ventaService.generarVenta(venta);
                return ResponseEntity.ok(guardada);
            }

            return ResponseEntity.badRequest().build();

    }

    @GetMapping
    public List<Venta> obtenerTodas() {
        return ventaService.obtenerTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Venta> obtenerPorId(@PathVariable Long id) {
        Venta venta = ventaService.obtenerPorId(id);
        return venta != null ? ResponseEntity.ok(venta) : ResponseEntity.notFound().build();
    }

    @GetMapping("/clienteId/{clienteId}")
    public List<Venta> obtenerPorCliente(@PathVariable Long clienteId) {
        return ventaService.obtenerPorClienteID(clienteId);
    }

    @GetMapping("/factura/{id}")
    public ResponseEntity<String> generarFactura(@PathVariable Long id) {
        String factura = ventaService.generarFactura(id);
        return factura != null ? ResponseEntity.ok(factura) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("eliminar/{id}")
    public ResponseEntity<Void> eliminarVenta(@PathVariable Long id) {
        ventaService.eliminarVenta(id);
        return ResponseEntity.noContent().build();
    }
}
