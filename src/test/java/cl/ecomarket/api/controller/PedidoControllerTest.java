package cl.ecomarket.api.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import cl.ecomarket.api.model.Estados;
import cl.ecomarket.api.model.Pedido;
import cl.ecomarket.api.services.PedidoService;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PedidoController.class)
public class PedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PedidoService pedidoService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testListarTodo_OK() throws Exception {
        Pedido p1 = new Pedido();
        p1.setId(1L);
        Pedido p2 = new Pedido();
        p2.setId(2L);

        when(pedidoService.obtenerTodos()).thenReturn(Arrays.asList(p1, p2));

        mockMvc.perform(get("/api/v1/pedidos"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testListarTodo_NoContent() throws Exception {
        when(pedidoService.obtenerTodos()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/pedidos"))
            .andExpect(status().isNoContent());
    }

    @Test
    void testObtenerPorId_OK() throws Exception {
        Pedido pedido = new Pedido();
        pedido.setId(1L);

        when(pedidoService.encontrarPorId(1L)).thenReturn(pedido);

        mockMvc.perform(get("/api/v1/pedidos/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testObtenerPorId_NotFound() throws Exception {
        when(pedidoService.encontrarPorId(999L)).thenThrow(new Exception("Pedido no encontrado"));

        mockMvc.perform(get("/api/v1/pedidos/999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void testObtenerPorIdCliente_OK() throws Exception {
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        List<Pedido> pedidos = Arrays.asList(pedido);

        when(pedidoService.obtenerPorClienteId(1L)).thenReturn(pedidos);

        mockMvc.perform(get("/api/v1/pedidos/cliente/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testObtenerPorIdCliente_NotFound() throws Exception {
        when(pedidoService.obtenerPorClienteId(999L)).thenThrow(new Exception("Cliente no encontrado"));

        mockMvc.perform(get("/api/v1/pedidos/cliente/999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void testGuardarPedido_Created() throws Exception {
        Pedido pedido = new Pedido();
        pedido.setId(null);

        Pedido pedidoGuardado = new Pedido();
        pedidoGuardado.setId(1L);

        when(pedidoService.crearPedido(any(Pedido.class))).thenReturn(pedidoGuardado);

        mockMvc.perform(post("/api/v1/pedidos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pedido)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testGuardarPedido_BadRequest() throws Exception {
        Pedido pedido = new Pedido();

        when(pedidoService.crearPedido(any(Pedido.class))).thenThrow(new Exception("Error"));

        mockMvc.perform(post("/api/v1/pedidos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pedido)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testCambiarEstado_OK() throws Exception {
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setEstado(Estados.PENDIENTE);

        when(pedidoService.encontrarPorId(1L)).thenReturn(pedido);
        when(pedidoService.crearPedido(any(Pedido.class))).thenReturn(pedido);

        mockMvc.perform(patch("/api/v1/pedidos/cambiar-estado/1")
                .param("estado", "APROBADA"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.estado").value("APROBADA"));
    }

    @Test
    void testCambiarEstado_BadRequest_EstadoRechazada() throws Exception {
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setEstado(Estados.RECHAZADA);

        when(pedidoService.encontrarPorId(1L)).thenReturn(pedido);

        mockMvc.perform(patch("/api/v1/pedidos/cambiar-estado/1")
                .param("estado", "APROBADA"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testCambiarEstado_NotFound() throws Exception {
        when(pedidoService.encontrarPorId(999L)).thenThrow(new Exception("Pedido no encontrado"));

        mockMvc.perform(patch("/api/v1/pedidos/cambiar-estado/999")
                .param("estado", "APROBADA"))
            .andExpect(status().isNotFound());
    }
}
