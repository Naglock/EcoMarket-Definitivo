package cl.ecomarket.api;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import cl.ecomarket.api.repository.*;
import cl.ecomarket.api.services.VentaService;
import net.datafaker.Faker;
import cl.ecomarket.api.model.*;

@Profile("dev")
@Component 
public class DataLoader implements CommandLineRunner {
    
    @Autowired
    private InventarioRepository inventarioRepository;
    @Autowired
    private PedidoRepository pedidoRepository;
    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private TiendaRepository tiendaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private VentaService ventaService;

    @Override
    public void run(String... args) throws Exception {
        Faker faker = new Faker();
        Random random = new Random();

        // Generar datos de ejemplo para usuarios
        for (int i = 0; i < 10; i++) {
            Usuario usuario = new Usuario();
            usuario.setPnombre(faker.name().firstName());
            usuario.setAppaterno(faker.name().lastName());
            usuario.setCorreo(faker.internet().emailAddress());
            usuario.setDireccion(faker.address().fullAddress());
            usuarioRepository.save(usuario);
        }

        // Generar datos de ejemplo para productos
        for (int i = 0; i < 10; i++) {
            Producto producto = new Producto();
            producto.setNombre(faker.commerce().productName());
            producto.setDescripcion(faker.lorem().sentence());
            producto.setPrecio(Double.parseDouble(faker.commerce().price()));
            productoRepository.save(producto);
        }

        // Generar datos de ejemplo para tiendas
        for (int i = 0; i < 5; i++) {
            Tienda tienda = new Tienda();
            tienda.setNombre(faker.company().name());
            tienda.setDireccion(faker.address().fullAddress());
            tienda.setTelefono(faker.phoneNumber().phoneNumber());
            tiendaRepository.save(tienda);
        }

        // Generar datos de ejemplo para inventarios
        for (int i = 0; i < 5; i++) {
            Inventario inventario = new Inventario();
            inventario.setTienda(tiendaRepository.findById((long) i+1).orElse(null));
            for (int j = 0; j < 10; j++) {
                ProductoInventariado productoInventariado = new ProductoInventariado();
                productoInventariado.setProducto(productoRepository.findById((long) (j + 1)).orElse(null));
                productoInventariado.setStock(random.nextInt(100)+20);
                inventario.getProductosInventariados().add(productoInventariado);
            }
            inventarioRepository.save(inventario);
        }

        // Generar datos de ejemplo para pedidos
        for (int i = 0; i < 5; i++) {
            Pedido pedido = new Pedido();
            pedido.setCliente(usuarioRepository.findById((long) (random.nextInt(10) + 1)).orElse(null));
            pedido.setTienda(tiendaRepository.findById((long) (random.nextInt(5) + 1)).orElse(null));
            Set<Long> productosUsados = new HashSet<>();
            for (int j = 0; j < 3; j++) {
                Long productoId;
                do {
                    productoId = (long) (random.nextInt(10) + 1);
                } while (productosUsados.contains(productoId)); // Evitar productos duplicados
                productosUsados.add(productoId);
                ItemPedido itemPedido = new ItemPedido();
                itemPedido.setProducto(productoRepository.findById(productoId).orElse(null));
                itemPedido.setCantidad(random.nextInt(5) + 1);
                pedido.getItems().add(itemPedido);
            }
            pedido.setEstado(Estados.PENDIENTE); // Estado inicial del pedido   
            pedidoRepository.save(pedido);
        }

        // Generar datos de ejemplo para ventas
        Set<Long> pedidosUsados = new HashSet<>();
        for (int i = 0; i < 5; i++) {
            Venta venta = new Venta();
            Long pedidoId;
            do {
                pedidoId = (long) (random.nextInt(5) + 1);
            } while (pedidosUsados.contains(pedidoId)); // Evitar pedidos duplicados
            pedidosUsados.add(pedidoId);
            venta.setPedido(pedidoRepository.findById(pedidoId).orElse(null));
            ventaService.generarVenta(venta);
        }
    }
}
