package cl.ecomarket.api.controller;

import cl.ecomarket.api.model.Producto;
import cl.ecomarket.api.services.ProductoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductoController.class)
public class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductoService productoService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void listarTodos_DevuelveListaProductos() throws Exception {
        Producto p1 = new Producto();
        p1.setId(1L);
        p1.setNombre("Prod1");
        p1.setPrecio(100);
        Producto p2 = new Producto();
        p2.setId(2L);
        p2.setNombre("Prod2");
        p2.setPrecio(200);

        Mockito.when(productoService.obtenerTodos()).thenReturn(Arrays.asList(p1, p2));

        mockMvc.perform(get("/api/v1/productos"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].nombre").value("Prod1"))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].precio").value(200));
    }

    @Test
    void listarTodos_DevuelveNoContentSiListaVacia() throws Exception {
        Mockito.when(productoService.obtenerTodos()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/productos"))
            .andExpect(status().isNoContent());
    }

    @Test
    void obtenerPorProductoId_ProductoExiste() throws Exception {
        Producto p = new Producto();
        p.setId(1L);
        p.setNombre("ProductoTest");
        p.setPrecio(300);

        Mockito.when(productoService.obtenerPorId(1L)).thenReturn(p);

        mockMvc.perform(get("/api/v1/productos/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.nombre").value("ProductoTest"))
            .andExpect(jsonPath("$.precio").value(300));
    }

    @Test
    void obtenerPorProductoId_ProductoNoExiste() throws Exception {
        Mockito.when(productoService.obtenerPorId(1L)).thenThrow(new Exception("Producto no encontrado"));

        mockMvc.perform(get("/api/v1/productos/1"))
            .andExpect(status().isNotFound());
    }

    @Test
    void guardarProducto_Correcto() throws Exception {
        Producto p = new Producto();
        p.setNombre("NuevoProd");
        p.setPrecio(400);

        Producto pGuardado = new Producto();
        pGuardado.setId(10L);
        pGuardado.setNombre("NuevoProd");
        pGuardado.setPrecio(400);

        Mockito.when(productoService.agregarProducto(any(Producto.class))).thenReturn(pGuardado);

        mockMvc.perform(post("/api/v1/productos")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(p)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(10))
            .andExpect(jsonPath("$.nombre").value("NuevoProd"))
            .andExpect(jsonPath("$.precio").value(400));
    }

    @Test
    void actualizarPrecio_Exitoso() throws Exception {
        Producto p = new Producto();
        p.setId(5L);
        p.setNombre("ProductoActualizado");
        p.setPrecio(999);

        Mockito.when(productoService.actualizarPrecio(eq(999.0), eq(5L))).thenReturn(p);

        mockMvc.perform(patch("/api/v1/productos")
            .param("ProductoId", "5")
            .param("Precio", "999"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(5))
            .andExpect(jsonPath("$.precio").value(999));
    }

    @Test
    void actualizarPrecio_Error() throws Exception {
        Mockito.when(productoService.actualizarPrecio(anyDouble(), anyLong())).thenThrow(new Exception("Error en precio"));

        mockMvc.perform(patch("/api/v1/productos")
            .param("ProductoId", "5")
            .param("Precio", "0"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void eliminarProducto_Exitoso() throws Exception {
        Mockito.doNothing().when(productoService).eliminarProducto(5L);

        mockMvc.perform(delete("/api/v1/productos")
            .param("ProductoId", "5"))
            .andExpect(status().isNoContent());
    }

    @Test
    void eliminarProducto_NoExiste() throws Exception {
        Mockito.doThrow(new Exception("Producto no encontrado")).when(productoService).eliminarProducto(5L);

        mockMvc.perform(delete("/api/v1/productos")
            .param("ProductoId", "5"))
            .andExpect(status().isNotFound());
    }
}
