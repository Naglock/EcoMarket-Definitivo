package cl.ecomarket.api.service;

import cl.ecomarket.api.model.*;
import cl.ecomarket.api.repository.VentaRepository;
import cl.ecomarket.api.services.PedidoService;
import cl.ecomarket.api.services.VentaService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VentaServiceTest {

    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private PedidoService pedidoService;

    @InjectMocks
    private VentaService ventaService;

    @Test
    void testObtenerPorId_Existe() throws Exception {
        Venta venta = new Venta();
        venta.setId(1L);

        when(ventaRepository.existsById(1L)).thenReturn(true);
        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));

        Venta resultado = ventaService.obtenerPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }

    @Test
    void testObtenerPorId_NoExiste() {
        when(ventaRepository.existsById(1L)).thenReturn(false);

        Exception ex = assertThrows(Exception.class, () -> ventaService.obtenerPorId(1L));
        assertEquals("Venta no encontrada", ex.getMessage());
    }

    @Test
    void testObtenerPorClienteID_ConVentas() throws Exception {
        Venta v1 = new Venta();
        Venta v2 = new Venta();

        when(ventaRepository.findByPedidoClienteId(1L)).thenReturn(List.of(v1, v2));

        List<Venta> ventas = ventaService.obtenerPorClienteID(1L);

        assertEquals(2, ventas.size());
    }

    @Test
    void testObtenerPorClienteID_SinVentas() {
        when(ventaRepository.findByPedidoClienteId(1L)).thenReturn(List.of());

        Exception ex = assertThrows(Exception.class, () -> ventaService.obtenerPorClienteID(1L));
        assertEquals("No se encontraron ventas para el cliente con ID: 1", ex.getMessage());
    }

    @Test
    void testGenerarFactura_ConVenta() throws Exception {
        Venta venta = new Venta();
        venta.setId(1L);
        venta.setTotal(1000.0);
        venta.setFecha(LocalDate.of(2024, 1, 1));

        Pedido pedido = new Pedido();
        Usuario cliente = new Usuario();
        cliente.setId(10L);
        pedido.setCliente(cliente);

        Producto producto = new Producto();
        producto.setId(5L);
        producto.setPrecio(500.0);

        ItemPedido item = new ItemPedido();
        item.setProducto(producto);
        item.setCantidad(2);
        pedido.setItems(List.of(item));

        venta.setPedido(pedido);

        when(ventaRepository.existsById(1L)).thenReturn(true);
        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));

        String factura = ventaService.generarFactura(1L);

        assertTrue(factura.contains("N° Venta: 1"));
        assertTrue(factura.contains("Cliente ID: 10"));
        assertTrue(factura.contains("Subtotal: 1000.0"));
    }

    @Test
    void testGenerarFactura_NoExiste() {
        when(ventaRepository.existsById(1L)).thenReturn(false);

        Exception ex = assertThrows(Exception.class, () -> ventaService.generarFactura(1L));
        assertEquals("Venta no encontrada", ex.getMessage());
    }

    @Test
    void testGenerarVenta_PedidoValido() throws Exception {
        Venta venta = new Venta();

        Pedido pedido = new Pedido();
        pedido.setId(1L);

        Producto producto = new Producto();
        producto.setId(1L);
        producto.setPrecio(500.0);

        ItemPedido item = new ItemPedido();
        item.setCantidad(2);
        item.setProducto(producto);
        pedido.setItems(List.of(item));

        venta.setPedido(pedido);

        when(pedidoService.existePorId(1L)).thenReturn(true);
        when(pedidoService.encontrarPorId(1L)).thenReturn(pedido);
        when(pedidoService.aprobarPedido(1L)).thenReturn(pedido);
        when(ventaRepository.save(any(Venta.class))).thenAnswer(inv -> inv.getArgument(0));

        Venta resultado = ventaService.generarVenta(venta);

        assertEquals(1000.0, resultado.getTotal());
        assertEquals(Estados.APROBADA, resultado.getEstado());
        assertNotNull(resultado.getFecha());
    }

    @Test
    void testGenerarVenta_PedidoNoExiste() throws Exception {
        Venta venta = new Venta();
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        venta.setPedido(pedido);

        when(pedidoService.existePorId(1L)).thenReturn(false);

        Exception ex = assertThrows(Exception.class, () -> ventaService.generarVenta(venta));
        assertTrue(ex.getMessage().contains("Error al generar la venta: El pedido no existe"));
    }

    @Test
    void testEliminarVenta_Existe() throws Exception {
        when(ventaRepository.existsById(1L)).thenReturn(true);

        ventaService.eliminarVenta(1L);

        verify(ventaRepository).deleteById(1L);
    }

    @Test
    void testEliminarVenta_NoExiste() {
        when(ventaRepository.existsById(1L)).thenReturn(false);

        Exception ex = assertThrows(Exception.class, () -> ventaService.eliminarVenta(1L));
        assertEquals("Venta no encontrada", ex.getMessage());
    }
}
