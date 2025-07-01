package cl.ecomarket.api.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import cl.ecomarket.api.model.Tienda;
import cl.ecomarket.api.services.TiendaService;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TiendaController.class)
public class TiendaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TiendaService tiendaService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testObtenerTodasLasTiendas_OK() throws Exception {
        Tienda tienda1 = new Tienda();
        tienda1.setId(1L);
        tienda1.setNombre("Tienda 1");

        Tienda tienda2 = new Tienda();
        tienda2.setId(2L);
        tienda2.setNombre("Tienda 2");

        when(tiendaService.obtenerTodasLasTiendas()).thenReturn(Arrays.asList(tienda1, tienda2));

        mockMvc.perform(get("/api/v1/tiendas"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].nombre").value("Tienda 1"))
            .andExpect(jsonPath("$[1].nombre").value("Tienda 2"));
    }

    @Test
    void testObtenerTodasLasTiendas_NoContent() throws Exception {
        when(tiendaService.obtenerTodasLasTiendas()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/tiendas"))
            .andExpect(status().isNoContent());
    }

    @Test
    void testObtenerTiendaPorId_OK() throws Exception {
        Tienda tienda = new Tienda();
        tienda.setId(1L);
        tienda.setNombre("Tienda 1");

        when(tiendaService.obtenerTiendaPorId(1L)).thenReturn(tienda);

        mockMvc.perform(get("/api/v1/tiendas/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.nombre").value("Tienda 1"));
    }

    @Test
    void testObtenerTiendaPorId_NotFound() throws Exception {
        when(tiendaService.obtenerTiendaPorId(999L)).thenThrow(new Exception("Tienda no encontrada"));

        mockMvc.perform(get("/api/v1/tiendas/999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void testCrearTienda_Created() throws Exception {
        Tienda tienda = new Tienda();
        tienda.setNombre("Tienda Nueva");

        Tienda tiendaGuardada = new Tienda();
        tiendaGuardada.setId(1L);
        tiendaGuardada.setNombre("Tienda Nueva");

        when(tiendaService.guardarTienda(any(Tienda.class))).thenReturn(tiendaGuardada);

        mockMvc.perform(post("/api/v1/tiendas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tienda)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.nombre").value("Tienda Nueva"));
    }

    @Test
    void testCrearTienda_BadRequest() throws Exception {
        Tienda tienda = new Tienda();
        tienda.setId(1L); // Simulando un ID ya existente

        when(tiendaService.guardarTienda(any(Tienda.class))).thenThrow(new Exception("Error"));

        mockMvc.perform(post("/api/v1/tiendas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tienda)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testEliminarTienda_NoContent() throws Exception {
        doNothing().when(tiendaService).eliminarTienda(1L);

        mockMvc.perform(delete("/api/v1/tiendas/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    void testEliminarTienda_NotFound() throws Exception {
        doThrow(new Exception("Tienda no encontrada")).when(tiendaService).eliminarTienda(999L);

        mockMvc.perform(delete("/api/v1/tiendas/999"))
            .andExpect(status().isNotFound());
    }
}
