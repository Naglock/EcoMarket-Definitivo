package cl.ecomarket.api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.*;

import cl.ecomarket.api.model.*;
import cl.ecomarket.api.repository.*;
import cl.ecomarket.api.services.InventarioService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class InventarioServiceTest {

    @Mock
    private InventarioRepository inventarioRepository;

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private TiendaRepository tiendaRepository;

    @Spy
    @InjectMocks
    private InventarioService inventarioService;

    @Test
    void testListarInventarioPorTiendaId_Existe() throws Exception {
        Long tiendaId = 1L;
        when(tiendaRepository.existsById(tiendaId)).thenReturn(true);

        Inventario inv = new Inventario();
        inv.setId(10L);
        when(inventarioRepository.findByTiendaId(tiendaId)).thenReturn(List.of(inv));

        List<Inventario> resultado = inventarioService.listarInventarioPorTiendaId(tiendaId);

        assertEquals(1, resultado.size());
        verify(tiendaRepository).existsById(tiendaId);
        verify(inventarioRepository).findByTiendaId(tiendaId);
    }

    @Test
    void testListarInventarioPorTiendaId_NoExiste() {
        Long tiendaId = 2L;
        when(tiendaRepository.existsById(tiendaId)).thenReturn(false);

        Exception ex = assertThrows(Exception.class,
            () -> inventarioService.listarInventarioPorTiendaId(tiendaId));

        assertEquals("La tienda con ID " + tiendaId + " no existe", ex.getMessage());
    }

    @Test
    void testCrearInventario_Exitoso() throws Exception {
        Tienda tienda = new Tienda();
        tienda.setId(1L);

        Producto producto = new Producto();
        producto.setId(1L);

        ProductoInventariado prodInv = new ProductoInventariado();
        prodInv.setProducto(producto);
        prodInv.setStock(null); // para probar que se setea a 0

        Inventario inventario = new Inventario();
        inventario.setTienda(tienda);
        inventario.setProductosInventariados(List.of(prodInv));

        when(tiendaRepository.existsById(tienda.getId())).thenReturn(true);
        when(tiendaRepository.findById(tienda.getId())).thenReturn(Optional.of(tienda));
        when(productoRepository.existsById(producto.getId())).thenReturn(true);
        when(productoRepository.findById(producto.getId())).thenReturn(Optional.of(producto));
        when(inventarioRepository.save(any(Inventario.class))).thenAnswer(i -> i.getArgument(0));

        Inventario resultado = inventarioService.crearInventario(inventario);

        assertNotNull(resultado);
        assertEquals(tienda, resultado.getTienda());
        assertNotNull(resultado.getProductosInventariados());
        assertEquals(0, resultado.getProductosInventariados().get(0).getStock());
        verify(inventarioRepository).save(any(Inventario.class));
    }

    @Test
    void testCrearInventario_TiendaNoExiste() {
        Tienda tienda = new Tienda();
        tienda.setId(99L);

        Inventario inventario = new Inventario();
        inventario.setTienda(tienda);
        inventario.setProductosInventariados(Collections.emptyList());

        when(tiendaRepository.existsById(tienda.getId())).thenReturn(false);

        Exception ex = assertThrows(Exception.class, () -> inventarioService.crearInventario(inventario));
        assertEquals("La tienda no existe", ex.getMessage());
    }

    @Test
    void testCrearInventario_ProductoNoExiste() {
        Tienda tienda = new Tienda();
        tienda.setId(1L);

        Producto producto = new Producto();
        producto.setId(100L);

        ProductoInventariado prodInv = new ProductoInventariado();
        prodInv.setProducto(producto);

        Inventario inventario = new Inventario();
        inventario.setTienda(tienda);
        inventario.setProductosInventariados(List.of(prodInv));

        when(tiendaRepository.existsById(tienda.getId())).thenReturn(true);
        when(productoRepository.existsById(producto.getId())).thenReturn(false);

        Exception ex = assertThrows(Exception.class, () -> inventarioService.crearInventario(inventario));
        assertEquals("El producto con ID " + producto.getId() + " no existe", ex.getMessage());
    }

    @Test
    void testEliminarInventario_Existe() throws Exception {
        Long inventarioId = 1L;
        when(inventarioRepository.existsById(inventarioId)).thenReturn(true);

        inventarioService.eliminarInventario(inventarioId);

        verify(inventarioRepository).deleteById(inventarioId);
    }

    @Test
    void testEliminarInventario_NoExiste() {
        Long inventarioId = 2L;
        when(inventarioRepository.existsById(inventarioId)).thenReturn(false);

        Exception ex = assertThrows(Exception.class, () -> inventarioService.eliminarInventario(inventarioId));
        assertEquals("El inventario con ID " + inventarioId + " no existe", ex.getMessage());
    }

    @Test
    void testEncontrarPorId_Existe() {
        Long inventarioId = 1L;
        Inventario inventario = new Inventario();
        inventario.setId(inventarioId);

        when(inventarioRepository.findById(inventarioId)).thenReturn(Optional.of(inventario));

        Inventario resultado = inventarioService.encontrarPorId(inventarioId);

        assertNotNull(resultado);
        assertEquals(inventarioId, resultado.getId());
    }

    @Test
    void testEncontrarPorId_NoExiste() {
        Long inventarioId = 2L;
        when(inventarioRepository.findById(inventarioId)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> inventarioService.encontrarPorId(inventarioId));
        assertEquals("El inventario con ID " + inventarioId + " no existe", ex.getMessage());
    }

    @Test
    void testEncontrarPorTienda_Existe() {
        Long tiendaId = 1L;
        Tienda tienda = new Tienda();
        tienda.setId(tiendaId);

        Inventario inventario = new Inventario();
        inventario.setId(10L);
        inventario.setTienda(tienda);

        when(tiendaRepository.existsById(tiendaId)).thenReturn(true);
        when(inventarioRepository.findByTiendaId(tiendaId)).thenReturn(List.of(inventario));

        Inventario resultado = inventarioService.encontrarPorTienda(tiendaId);

        assertNotNull(resultado);
        assertEquals(10L, resultado.getId());
    }

    @Test
    void testEncontrarPorTienda_TiendaNoExiste() {
        Long tiendaId = 2L;
        when(tiendaRepository.existsById(tiendaId)).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> inventarioService.encontrarPorTienda(tiendaId));
        assertEquals("La tienda con ID " + tiendaId + " no existe", ex.getMessage());
    }

    @Test
    void testEncontrarPorTienda_NoHayInventario() {
        Long tiendaId = 3L;
        when(tiendaRepository.existsById(tiendaId)).thenReturn(true);
        when(inventarioRepository.findByTiendaId(tiendaId)).thenReturn(Collections.emptyList());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> inventarioService.encontrarPorTienda(tiendaId));
        assertEquals("No se encontró inventario para la tienda con ID " + tiendaId, ex.getMessage());
    }

    @Test
    void testActualizarStock_Agregar() throws Exception {
        Long tiendaId = 1L;
        Long productoId = 1L;

        Producto producto = new Producto();
        producto.setId(productoId);

        ProductoInventariado prodInv = new ProductoInventariado();
        prodInv.setProducto(producto);
        prodInv.setStock(5);

        Inventario inventario = new Inventario();
        inventario.setId(1L);
        inventario.setProductosInventariados(new ArrayList<>(List.of(prodInv)));

        // Mock repositorios
        when(tiendaRepository.existsById(tiendaId)).thenReturn(true);
        when(inventarioRepository.findByTiendaId(tiendaId)).thenReturn(List.of(inventario));
        when(inventarioRepository.save(any(Inventario.class))).thenAnswer(i -> i.getArgument(0));

        // Ejecutar método real
        Inventario resultado = inventarioService.actualizarStock(tiendaId, productoId, 3, "agregar");

        // Validar resultados
        assertNotNull(resultado);
        assertEquals(8, resultado.getProductosInventariados().get(0).getStock());
        verify(inventarioRepository).save(inventario);
    }


    @Test
    void testActualizarStock_Quitar_SuficienteStock() throws Exception {
        Long tiendaId = 1L;
        Long productoId = 1L;

        // Preparar datos
        Producto producto = new Producto();
        producto.setId(productoId);

        ProductoInventariado prodInv = new ProductoInventariado();
        prodInv.setProducto(producto);
        prodInv.setStock(10);

        Inventario inventario = new Inventario();
        inventario.setId(1L);
        inventario.setProductosInventariados(new ArrayList<>(List.of(prodInv)));

        // Simulaciones
        when(tiendaRepository.existsById(tiendaId)).thenReturn(true);
        when(inventarioRepository.findByTiendaId(tiendaId)).thenReturn(List.of(inventario));
        when(inventarioRepository.save(any(Inventario.class))).thenAnswer(i -> i.getArgument(0));

        // Ejecutar método
        Inventario resultado = inventarioService.actualizarStock(tiendaId, productoId, 3, "quitar");

        // Validaciones
        assertNotNull(resultado);
        assertEquals(7, resultado.getProductosInventariados().get(0).getStock());
    }


    @Test
    void testActualizarStock_Quitar_NoSuficienteStock() throws Exception {
        Long tiendaId = 1L;
        Long productoId = 1L;

        Producto producto = new Producto();
        producto.setId(productoId);

        ProductoInventariado prodInv = new ProductoInventariado();
        prodInv.setProducto(producto);
        prodInv.setStock(2);

        Inventario inventario = new Inventario();
        inventario.setId(1L);
        inventario.setProductosInventariados(new ArrayList<>(List.of(prodInv)));

        doReturn(inventario).when(inventarioService).encontrarPorTienda(tiendaId);

        Exception ex = assertThrows(Exception.class,() -> inventarioService.actualizarStock(tiendaId, productoId, 5, "quitar"));
        assertEquals("No hay suficiente stock para quitar", ex.getMessage());
        }
    @Test
    void testHayStock_True() {
        Long tiendaId = 1L;
        Long productoId = 1L;

        Producto producto = new Producto();
        producto.setId(productoId);

        ProductoInventariado prodInv = new ProductoInventariado();
        prodInv.setProducto(producto);
        prodInv.setStock(10);

        Inventario inventario = new Inventario();
        inventario.setProductosInventariados(List.of(prodInv));

        doReturn(inventario).when(inventarioService).encontrarPorTienda(tiendaId);

        boolean hayStock = inventarioService.hayStock(productoId, tiendaId, 5);
        assertTrue(hayStock);
    }

    @Test
    void testHayStock_False_ProductoNoExiste() {
        Long tiendaId = 1L;
        Long productoId = 1L;

        Inventario inventario = new Inventario();
        inventario.setProductosInventariados(Collections.emptyList());

        doReturn(inventario).when(inventarioService).encontrarPorTienda(tiendaId);

        boolean hayStock = inventarioService.hayStock(productoId, tiendaId, 5);
        assertFalse(hayStock);
    }
}

