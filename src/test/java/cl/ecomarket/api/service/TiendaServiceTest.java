package cl.ecomarket.api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import cl.ecomarket.api.model.Tienda;
import cl.ecomarket.api.repository.TiendaRepository;
import cl.ecomarket.api.services.TiendaService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TiendaServiceTest {

    @Mock
    private TiendaRepository tiendaRepository;

    @InjectMocks
    private TiendaService tiendaService;

    @Test
    void testObtenerTodasLasTiendas() throws Exception {
        Tienda t1 = new Tienda();
        Tienda t2 = new Tienda();
        when(tiendaRepository.findAll()).thenReturn(Arrays.asList(t1, t2));

        List<Tienda> resultado = tiendaService.obtenerTodasLasTiendas();

        assertEquals(2, resultado.size());
        verify(tiendaRepository).findAll();
    }

    @Test
    void testObtenerTiendaPorId_Existe() throws Exception {
        Tienda tienda = new Tienda();
        tienda.setId(1L);

        when(tiendaRepository.existsById(1L)).thenReturn(true);
        when(tiendaRepository.findById(1L)).thenReturn(Optional.of(tienda));

        Tienda resultado = tiendaService.obtenerTiendaPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }

    @Test
    void testObtenerTiendaPorId_NoExiste() {
        when(tiendaRepository.existsById(1L)).thenReturn(false);
        Exception ex = assertThrows(Exception.class, () -> tiendaService.obtenerTiendaPorId(1L));
        assertEquals("Tienda no encontrada", ex.getMessage());
    }

    @Test
    void testGuardarTienda() throws Exception {
        Tienda tienda = new Tienda();
        tienda.setNombre("Tienda ABC");

        when(tiendaRepository.save(tienda)).thenReturn(tienda);

        Tienda resultado = tiendaService.guardarTienda(tienda);

        assertNotNull(resultado);
        assertEquals("Tienda ABC", resultado.getNombre());
        verify(tiendaRepository).save(tienda);
    }

    @Test
    void testEliminarTienda_Existe() throws Exception {
        when(tiendaRepository.existsById(1L)).thenReturn(true);

        tiendaService.eliminarTienda(1L);

        verify(tiendaRepository).deleteById(1L);
    }

    @Test
    void testEliminarTienda_NoExiste() {
        when(tiendaRepository.existsById(1L)).thenReturn(false);

        Exception ex = assertThrows(Exception.class, () -> tiendaService.eliminarTienda(1L));
        assertEquals("Tienda no encontrada", ex.getMessage());
    }
}
