package cl.ecomarket.api.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.ecomarket.api.model.Estados;
import cl.ecomarket.api.model.Inventario;
import cl.ecomarket.api.model.ItemPedido;
import cl.ecomarket.api.model.Pedido;
import cl.ecomarket.api.model.ProductoInventariado;
import cl.ecomarket.api.repository.PedidoRepository;
import cl.ecomarket.api.repository.TiendaRepository;
import cl.ecomarket.api.repository.UsuarioRepository;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private TiendaRepository tiendaRepository;
    @Autowired
    private InventarioService inventarioService;

    public List<Pedido> obtenerTodos() {
        return pedidoRepository.findAll();
    }

    public Pedido crearPedido(Pedido pedido) throws Exception {
        // Verificar si el cliente existe
        if (pedido.getCliente() == null || !usuarioRepository.existsById(pedido.getCliente().getId())) {
            throw new Exception("El cliente no existe");
        }
        pedido.setCliente(usuarioRepository.findById(pedido.getCliente().getId()).get()); // vincular el cliente del pedido con el cliente de la base de datos
        // Verificar si la tienda existe
        if (pedido.getTienda() == null || !tiendaRepository.existsById(pedido.getTienda().getId())) {
            throw new Exception("La tienda no existe");
        }
        pedido.setTienda(tiendaRepository.findById(pedido.getTienda().getId()).get()); // vincular la tienda del pedido con la tienda de la base de datos
        if (pedido.getEstado() == null) {
            pedido.setEstado(Estados.PENDIENTE); // Establecer el estado del pedido como PENDIENTE si no se especifica
        }
        // Verificar si los productos existen en la lista de productos inventariados del inventario segun la tienda
        Inventario inventario = inventarioService.encontrarPorTienda(pedido.getTienda().getId());
        for (ItemPedido item : pedido.getItems()) {
            ProductoInventariado productoInventariado = inventario.getProductosInventariados()
                    .stream()
                    .filter(pi -> pi.getProducto().getId().equals(item.getProducto().getId()))
                    .findFirst()
                    .orElse(null);
            if (productoInventariado == null || productoInventariado.getStock() < item.getCantidad()) {
                throw new Exception("Producto no disponible en inventario o stock insuficiente");
            // vincular el producto del pedido con el producto del inventario
            } else {
                item.setProducto(productoInventariado.getProducto());
            }        
        }
        // Si se verifican las condiciones, se descuenta el stock del inventario
        for (ItemPedido item : pedido.getItems()) {
            ProductoInventariado productoInventariado = inventario.getProductosInventariados()
                    .stream()
                    .filter(pi -> pi.getProducto().getId().equals(item.getProducto().getId()))
                    .findFirst()
                    .orElse(null);
            if (productoInventariado != null) {
                inventarioService.actualizarStock(pedido.getTienda().getId(), productoInventariado.getProducto().getId(), item.getCantidad(), "quitar");
            }
        }
        // Guardar el pedido en la base de datos
        return pedidoRepository.save(pedido);
    }

    public Pedido encontrarPorId(Long id) throws Exception {
        // Verificar si el pedido existe
        if (!pedidoRepository.existsById(id)) {
            throw new Exception("Pedido no encontrado");
        }   
        return pedidoRepository.findById(id).get();
    }

    public boolean existePorId(Long id) throws Exception {
        // Verificar si el pedido existe
        if (id == null) {
            throw new Exception("El ID del pedido no puede ser nulo");
        }
        return pedidoRepository.existsById(id);
    }

    public List<Pedido> obtenerPorClienteId(Long ClienteId) throws Exception {
        // verificar si el cliente existe
        if (!usuarioRepository.existsById(ClienteId)) {
            throw new Exception("El cliente con ID " + ClienteId + " no existe");
        }
        // buscar los pedidos por el ID del cliente
        return pedidoRepository.findByClienteId(ClienteId);
    }
}
