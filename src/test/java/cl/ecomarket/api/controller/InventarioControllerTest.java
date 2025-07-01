package cl.ecomarket.api.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import cl.ecomarket.api.model.Inventario;
import cl.ecomarket.api.model.ProductoInventariado;
import cl.ecomarket.api.services.InventarioService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(InventarioController.class)
public class InventarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InventarioService inventarioService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testListarTodos_OK() throws Exception {
        Inventario inv1 = new Inventario();
        Inventario inv2 = new Inventario();

        when(inventarioService.listarTodos()).thenReturn(Arrays.asList(inv1, inv2));

        mockMvc.perform(get("/api/v1/inventario/tiendas"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testListarTodos_NoContent() throws Exception {
        when(inventarioService.listarTodos()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/inventario/tiendas"))
            .andExpect(status().isNoContent());
    }

    @Test
    void testListarPorIdTienda_OK() throws Exception {
        Inventario inventario = new Inventario();
        ProductoInventariado productoInventariado = new ProductoInventariado();
        inventario.setProductosInventariados(Arrays.asList(productoInventariado));

        when(inventarioService.encontrarPorTienda(1L)).thenReturn(inventario);

        mockMvc.perform(get("/api/v1/inventario/tienda/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testListarPorIdTienda_NotFound() throws Exception {
        when(inventarioService.encontrarPorTienda(999L)).thenThrow(new RuntimeException("No encontrado"));

        mockMvc.perform(get("/api/v1/inventario/tienda/999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void testGuardarInventario_Created() throws Exception {
        Inventario inventario = new Inventario();

        when(inventarioService.crearInventario(any(Inventario.class))).thenReturn(inventario);

        mockMvc.perform(post("/api/v1/inventario")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inventario)))
            .andExpect(status().isCreated());
    }

    @Test
    void testGuardarInventario_BadRequest() throws Exception {
        Inventario inventario = new Inventario();

        when(inventarioService.crearInventario(any(Inventario.class))).thenThrow(new Exception("Error"));

        mockMvc.perform(post("/api/v1/inventario")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inventario)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testActualizarStock_OK() throws Exception {
        Inventario inventario = new Inventario();

        when(inventarioService.actualizarStock(1L, 2L, 10, "agregar")).thenReturn(inventario);

        mockMvc.perform(patch("/api/v1/inventario")
                .param("tiendaId", "1")
                .param("productoId", "2")
                .param("operacion", "agregar")
                .param("stock", "10"))
            .andExpect(status().isOk());
    }

    @Test
    void testActualizarStock_BadRequest() throws Exception {
        when(inventarioService.actualizarStock(anyLong(), anyLong(), anyInt(), anyString())).thenThrow(new Exception("Error"));

        mockMvc.perform(patch("/api/v1/inventario")
                .param("tiendaId", "1")
                .param("productoId", "2")
                .param("operacion", "quitar")
                .param("stock", "5"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testEliminarInventario_NoContent() throws Exception {
        doNothing().when(inventarioService).eliminarInventario(1L);

        mockMvc.perform(delete("/api/v1/inventario/eliminar/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    void testEliminarInventario_NotFound() throws Exception {
        doThrow(new Exception("No encontrado")).when(inventarioService).eliminarInventario(999L);

        mockMvc.perform(delete("/api/v1/inventario/eliminar/999"))
            .andExpect(status().isNotFound());
    }
}
