package cl.ecomarket.api.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.ecomarket.api.model.ItemPedido;
import cl.ecomarket.api.model.Pedido;
import cl.ecomarket.api.services.InventarioService;
import cl.ecomarket.api.services.PedidoService;
import cl.ecomarket.api.services.ProductoService;
import cl.ecomarket.api.services.TiendaService;
import cl.ecomarket.api.services.UsuarioService;

@RestController
@RequestMapping("/api/v1/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired 
    private ProductoService productoService;
    @Autowired
    private InventarioService inventarioService;
    @Autowired
    private TiendaService tiendaService;

    @GetMapping
    public ResponseEntity<List<Pedido>> listarTodo(){
        List<Pedido> pedidos = pedidoService.obtenerTodos();
        if (pedidos.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/{idPedido}")
    public ResponseEntity<Pedido> obtenerPorId(@PathVariable Long idPedido) {
        try {
            Pedido pedido = pedidoService.encontrarPorId(idPedido);
            return ResponseEntity.ok(pedido);
        } catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<Pedido>> obtenerPorIdCliente(@PathVariable Long idCliente){
        List<Pedido> pedidos = pedidoService.obtenerPorClienteId(idCliente);
        if (pedidos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(pedidos);
    }

    @PostMapping
    public ResponseEntity<Pedido> guardarPedido(@RequestBody Pedido pedido) {
        List<ItemPedido> productos = new ArrayList<>();
        pedido.setCliente(usuarioService.encontrarPorId(pedido.getCliente().getId())); //Busca el cliente que esta en pedido y lo vincula al que esta en la base de datos
        pedido.setTienda(tiendaService.obtenerTiendaPorId(pedido.getTienda().getId()));//Busca la tienda que aparece en pedido y la vincula a la de la base de datos
        for (ItemPedido items : pedido.getItems()){
            ItemPedido listado = new ItemPedido();
            listado.setCantidad(items.getCantidad());
            listado.setProducto(productoService.obtenerPorId(items.getProducto().getId())); //Obtiene el producto con el id y lo vincula con el de la base de datos.
            if (!inventarioService.hayStock(listado.getProducto().getId(), pedido.getTienda().getId(), listado.getCantidad())){ // Revisa si hay stock para el pedido.
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            productos.add(listado);           // Si hay stock, agrega el listado ->EJ "Productos":[{"cantidad":10,"producto":{"id":1}},{"cantidad":5,"producto":{"id":2}}]
        }
        pedido.setItems(productos);
        Pedido nuevoPedido = pedidoService.crearPedido(pedido);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoPedido);
    }

}
