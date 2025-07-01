package cl.ecomarket.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.ecomarket.api.model.Estados;
import cl.ecomarket.api.model.Inventario;
import cl.ecomarket.api.model.ItemPedido;
import cl.ecomarket.api.model.Pedido;
import cl.ecomarket.api.model.Producto;
import cl.ecomarket.api.model.ProductoInventariado;
import cl.ecomarket.api.model.Tienda;
import cl.ecomarket.api.model.Usuario;
import cl.ecomarket.api.repository.PedidoRepository;
import cl.ecomarket.api.repository.TiendaRepository;
import cl.ecomarket.api.repository.UsuarioRepository;
import cl.ecomarket.api.services.InventarioService;
import cl.ecomarket.api.services.PedidoService;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @InjectMocks
    private PedidoService pedidoService;

    @Mock
    private PedidoRepository pedidoRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private TiendaRepository tiendaRepository;
    @Mock
    private InventarioService inventarioService;

    @Test
    void testCrearPedido_Exito() throws Exception {
        // IDs
        Long clienteId = 1L;
        Long tiendaId = 2L;
        Long productoId = 3L;

        // Cliente y tienda
        Usuario cliente = new Usuario();
        cliente.setId(clienteId);

        Tienda tienda = new Tienda();
        tienda.setId(tiendaId);

        Producto producto = new Producto();
        producto.setId(productoId);

        // Item del pedido
        ItemPedido item = new ItemPedido();
        item.setProducto(producto);
        item.setCantidad(2);

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setTienda(tienda);
        pedido.setItems(List.of(item));

        // Inventario con suficiente stock
        ProductoInventariado prodInv = new ProductoInventariado();
        prodInv.setProducto(producto);
        prodInv.setStock(10);

        Inventario inventario = new Inventario();
        inventario.setProductosInventariados(List.of(prodInv));

        when(usuarioRepository.existsById(clienteId)).thenReturn(true);
        when(usuarioRepository.findById(clienteId)).thenReturn(Optional.of(cliente));

        when(tiendaRepository.existsById(tiendaId)).thenReturn(true);
        when(tiendaRepository.findById(tiendaId)).thenReturn(Optional.of(tienda));

        when(inventarioService.encontrarPorTienda(tiendaId)).thenReturn(inventario);

        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(i -> i.getArgument(0));

        // Ejecutar
        Pedido resultado = pedidoService.crearPedido(pedido);

        // Verificar
        assertNotNull(resultado);
        assertEquals(Estados.PENDIENTE, resultado.getEstado());
        verify(inventarioService).actualizarStock(tiendaId, productoId, 2, "quitar");
        verify(pedidoRepository).save(any(Pedido.class));
    }
    @Test
    void testCrearPedido_ClienteNoExiste() {
        Pedido pedido = new Pedido();
        Usuario cliente = new Usuario();
        cliente.setId(1L);
        pedido.setCliente(cliente);

        when(usuarioRepository.existsById(1L)).thenReturn(false);

        Exception ex = assertThrows(Exception.class, () -> pedidoService.crearPedido(pedido));
        assertEquals("El cliente no existe", ex.getMessage());
    }
    @Test
    void testCrearPedido_StockInsuficiente() throws Exception {
        Long clienteId = 1L;
        Long tiendaId = 1L;
        Long productoId = 1L;

        Usuario cliente = new Usuario(); cliente.setId(clienteId);
        Tienda tienda = new Tienda(); tienda.setId(tiendaId);
        Producto producto = new Producto(); producto.setId(productoId);

        ItemPedido item = new ItemPedido();
        item.setProducto(producto);
        item.setCantidad(5);

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setTienda(tienda);
        pedido.setItems(List.of(item));

        ProductoInventariado productoInventariado = new ProductoInventariado();
        productoInventariado.setProducto(producto);
        productoInventariado.setStock(2); // insuficiente

        Inventario inventario = new Inventario();
        inventario.setProductosInventariados(List.of(productoInventariado));

        when(usuarioRepository.existsById(clienteId)).thenReturn(true);
        when(usuarioRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(tiendaRepository.existsById(tiendaId)).thenReturn(true);
        when(tiendaRepository.findById(tiendaId)).thenReturn(Optional.of(tienda));
        when(inventarioService.encontrarPorTienda(tiendaId)).thenReturn(inventario);

        Exception ex = assertThrows(Exception.class, () -> pedidoService.crearPedido(pedido));
        assertEquals("Producto no disponible en inventario o stock insuficiente", ex.getMessage());
    }
    @Test
    void testEncontrarPorId_Existe() throws Exception {
        Pedido pedido = new Pedido();
        pedido.setId(1L);

        when(pedidoRepository.existsById(1L)).thenReturn(true);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        Pedido resultado = pedidoService.encontrarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }
    @Test
    void testEncontrarPorId_NoExiste() {
        when(pedidoRepository.existsById(1L)).thenReturn(false);

        Exception ex = assertThrows(Exception.class, () -> pedidoService.encontrarPorId(1L));
        assertEquals("Pedido no encontrado", ex.getMessage());
    }
    @Test
    void testExistePorId_Valido() throws Exception {
        when(pedidoRepository.existsById(1L)).thenReturn(true);

        boolean resultado = pedidoService.existePorId(1L);

        assertTrue(resultado);
    }
    @Test
    void testExistePorId_IdNulo() {
        Exception ex = assertThrows(Exception.class, () -> pedidoService.existePorId(null));
        assertEquals("El ID del pedido no puede ser nulo", ex.getMessage());
    }
    @Test
    void testObtenerPorClienteId_ClienteExiste() throws Exception {
        Long clienteId = 1L;
        Pedido pedido1 = new Pedido();
        Pedido pedido2 = new Pedido();

        when(usuarioRepository.existsById(clienteId)).thenReturn(true);
        when(pedidoRepository.findByClienteId(clienteId)).thenReturn(List.of(pedido1, pedido2));

        List<Pedido> pedidos = pedidoService.obtenerPorClienteId(clienteId);

        assertEquals(2, pedidos.size());
    }
    @Test
    void testObtenerPorClienteId_ClienteNoExiste() {
        when(usuarioRepository.existsById(1L)).thenReturn(false);

        Exception ex = assertThrows(Exception.class, () -> pedidoService.obtenerPorClienteId(1L));
        assertEquals("El cliente con ID 1 no existe", ex.getMessage());
    }
    @Test
    void testAprobarPedido_Existe() throws Exception {
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setEstado(Estados.PENDIENTE);

        when(pedidoRepository.existsById(1L)).thenReturn(true);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(i -> i.getArgument(0));

        Pedido actualizado = pedidoService.aprobarPedido(1L);

        assertEquals(Estados.APROBADA, actualizado.getEstado());
        verify(pedidoRepository).save(pedido);
    }
    @Test
    void testAprobarPedido_NoExiste() {
        when(pedidoRepository.existsById(1L)).thenReturn(false);

        Exception ex = assertThrows(Exception.class, () -> pedidoService.aprobarPedido(1L));
        assertEquals("Pedido no encontrado", ex.getMessage());
    }
}
