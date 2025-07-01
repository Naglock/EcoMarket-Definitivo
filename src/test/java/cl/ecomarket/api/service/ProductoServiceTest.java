package cl.ecomarket.api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import cl.ecomarket.api.model.Producto;
import cl.ecomarket.api.repository.ProductoRepository;
import cl.ecomarket.api.services.ProductoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoService productoService;

    @Test
    void testObtenerTodos() {
        Producto p1 = new Producto();
        Producto p2 = new Producto();
        when(productoRepository.findAll()).thenReturn(Arrays.asList(p1, p2));

        List<Producto> resultado = productoService.obtenerTodos();

        assertEquals(2, resultado.size());
        verify(productoRepository).findAll();
    }

    @Test
    void testAgregarProducto() {
        Producto producto = new Producto();
        producto.setNombre("Leche");

        when(productoRepository.save(producto)).thenReturn(producto);

        Producto resultado = productoService.agregarProducto(producto);

        assertNotNull(resultado);
        assertEquals("Leche", resultado.getNombre());
        verify(productoRepository).save(producto);
    }

    @Test
    void testObtenerPorId_Existe() throws Exception {
        Producto producto = new Producto();
        producto.setId(1L);

        when(productoRepository.existsById(1L)).thenReturn(true);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        Producto resultado = productoService.obtenerPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }

    @Test
    void testObtenerPorId_NoExiste() {
        when(productoRepository.existsById(1L)).thenReturn(false);

        Exception ex = assertThrows(Exception.class, () -> productoService.obtenerPorId(1L));
        assertEquals("Producto no encontrado", ex.getMessage());
    }

    @Test
    void testEliminarProducto_Existe() throws Exception {
        when(productoRepository.existsById(1L)).thenReturn(true);

        productoService.eliminarProducto(1L);

        verify(productoRepository).deleteById(1L);
    }

    @Test
    void testEliminarProducto_NoExiste() {
        when(productoRepository.existsById(1L)).thenReturn(false);

        Exception ex = assertThrows(Exception.class, () -> productoService.eliminarProducto(1L));
        assertEquals("Producto no encontrado", ex.getMessage());
    }

    @Test
    void testActualizarPrecio_ProductoExisteYPrecioValido() throws Exception {
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setPrecio(1000);

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Producto actualizado = productoService.actualizarPrecio(1500, 1L);

        assertEquals(1500, actualizado.getPrecio());
        verify(productoRepository).save(producto);
    }

    @Test
    void testActualizarPrecio_PrecioInvalido() {
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setPrecio(1000);

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        Exception ex = assertThrows(Exception.class, () -> productoService.actualizarPrecio(0, 1L));
        assertEquals("El precio debe ser mayor a cero", ex.getMessage());
    }

    @Test
    void testActualizarPrecio_ProductoNoExiste() {
        when(productoRepository.findById(1L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(Exception.class, () -> productoService.actualizarPrecio(1000, 1L));
        assertEquals("Producto no encontrado", ex.getMessage());
    }
}
