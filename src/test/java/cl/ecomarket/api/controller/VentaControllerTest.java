package cl.ecomarket.api.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import cl.ecomarket.api.model.Estados;
import cl.ecomarket.api.model.Pedido;
import cl.ecomarket.api.model.Venta;
import cl.ecomarket.api.services.VentaService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(VentaController.class)
public class VentaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VentaService ventaService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testRegistrarVenta_Success() throws Exception {
        // Datos de ejemplo para la venta
        Venta venta = new Venta();
        venta.setId(1L);
        venta.setEstado(Estados.APROBADA);
        venta.setFecha(LocalDate.now());
        venta.setTotal(100.0);
        Pedido pedido = new Pedido();
        pedido.setId(10L);
        venta.setPedido(pedido);

        // Mockear el servicio
        when(ventaService.generarVenta(any(Venta.class))).thenReturn(venta);

        String jsonVenta = objectMapper.writeValueAsString(venta);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/ventas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonVenta)
                .param("estado", "APROBADA"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.estado").value("APROBADA"))
            .andExpect(jsonPath("$.total").value(100.0));
    }

    @Test
    void testRegistrarVenta_BadRequest() throws Exception {
        when(ventaService.generarVenta(any(Venta.class))).thenThrow(new Exception("Error"));

        String jsonVenta = "{}";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/ventas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonVenta)
                .param("estado", "APROBADA"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testObtenerTodas_Success() throws Exception {
        Venta venta = new Venta();
        venta.setId(1L);
        venta.setEstado(Estados.APROBADA);
        venta.setFecha(LocalDate.now());
        venta.setTotal(100.0);
        venta.setPedido(new Pedido());

        List<Venta> ventas = Arrays.asList(venta);

        when(ventaService.obtenerTodas()).thenReturn(ventas);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/ventas"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void testObtenerTodas_NoContent() throws Exception {
        when(ventaService.obtenerTodas()).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/ventas"))
            .andExpect(status().isNoContent());
    }

    @Test
    void testObtenerPorId_Success() throws Exception {
        Venta venta = new Venta();
        venta.setId(1L);

        when(ventaService.obtenerPorId(1L)).thenReturn(venta);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/ventas/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testObtenerPorId_NotFound() throws Exception {
        when(ventaService.obtenerPorId(1L)).thenThrow(new Exception("No encontrado"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/ventas/1"))
            .andExpect(status().isNotFound());
    }

    @Test
    void testEliminarVenta_Success() throws Exception {
        // No hace falta mockear nada para eliminar, solo que no lance excepción
        doNothing().when(ventaService).eliminarVenta(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/ventas/eliminar/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    void testEliminarVenta_NotFound() throws Exception {
        doThrow(new Exception("No encontrado")).when(ventaService).eliminarVenta(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/ventas/eliminar/1"))
            .andExpect(status().isNotFound());
    }
    @Test
    void testObtenerPorCliente_Success() throws Exception {
        Venta venta = new Venta();
        venta.setId(1L);
        venta.setEstado(Estados.APROBADA);
        venta.setFecha(LocalDate.now());
        venta.setTotal(100.0);
        venta.setPedido(new Pedido());

        List<Venta> ventas = Arrays.asList(venta);

        when(ventaService.obtenerPorClienteID(1L)).thenReturn(ventas);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/ventas/clienteId/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void testObtenerPorCliente_NoContent() throws Exception {
        when(ventaService.obtenerPorClienteID(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/ventas/clienteId/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    void testObtenerPorCliente_NotFound() throws Exception {
        when(ventaService.obtenerPorClienteID(1L)).thenThrow(new Exception("No encontrado"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/ventas/clienteId/1"))
            .andExpect(status().isNotFound());
    }

    @Test
    void testGenerarFactura_Success() throws Exception {
        Venta venta = new Venta();
        venta.setId(1L);
        venta.setPedido(new Pedido());
        venta.setTotal(100.0);
        venta.setFecha(LocalDate.now());

        String facturaMock = "Factura ejemplo";

        when(ventaService.obtenerPorId(1L)).thenReturn(venta);
        when(ventaService.generarFactura(1L)).thenReturn(facturaMock);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/ventas/factura/1"))
            .andExpect(status().isOk())
            .andExpect(content().string(facturaMock));
    }

    @Test
    void testGenerarFactura_NotFound() throws Exception {
        when(ventaService.obtenerPorId(1L)).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/ventas/factura/1"))
            .andExpect(status().isNotFound());
    }

    @Test
    void testGenerarFactura_InternalServerError() throws Exception {
        when(ventaService.obtenerPorId(1L)).thenThrow(new Exception("Error interno"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/ventas/factura/1"))
            .andExpect(status().isInternalServerError());
    }
}

