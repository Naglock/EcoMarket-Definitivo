package cl.ecomarket.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import cl.ecomarket.api.model.Usuario;
import cl.ecomarket.api.services.UsuarioService;

@WebMvcTest(UsuarioController.class)
public class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testListarUsuarios_OK() throws Exception {
        Usuario u1 = new Usuario();
        u1.setId(1L);
        u1.setPnombre("Juan");
        Usuario u2 = new Usuario();
        u2.setId(2L);
        u2.setPnombre("Maria");

        when(usuarioService.listarUsuarios()).thenReturn(List.of(u1, u2));

        mockMvc.perform(get("/api/v1/usuarios"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1L))
            .andExpect(jsonPath("$[0].pnombre").value("Juan"))
            .andExpect(jsonPath("$[1].id").value(2L))
            .andExpect(jsonPath("$[1].pnombre").value("Maria"));
    }

    @Test
    void testListarUsuarios_NoContent() throws Exception {
        when(usuarioService.listarUsuarios()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/usuarios"))
            .andExpect(status().isNoContent());
    }

    @Test
    void testGuardarUsuario_Created() throws Exception {
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setPnombre("Carlos");
        nuevoUsuario.setAppaterno("Perez");
        nuevoUsuario.setDireccion("Calle Falsa 123");

        Usuario guardado = new Usuario();
        guardado.setId(10L);
        guardado.setPnombre("Carlos");
        guardado.setAppaterno("Perez");
        guardado.setDireccion("Calle Falsa 123");

        when(usuarioService.guardarUsuario(any(Usuario.class))).thenReturn(guardado);

        mockMvc.perform(post("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevoUsuario)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(10L))
            .andExpect(jsonPath("$.pnombre").value("Carlos"));
    }

    @Test
    void testGuardarUsuario_BadRequest() throws Exception {
        Usuario usuarioInvalido = new Usuario();

        when(usuarioService.guardarUsuario(any(Usuario.class))).thenThrow(new Exception("Error"));

        mockMvc.perform(post("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioInvalido)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testBuscarUsuario_OK() throws Exception {
        Usuario u = new Usuario();
        u.setId(5L);
        u.setPnombre("Laura");

        when(usuarioService.encontrarPorId(5L)).thenReturn(u);

        mockMvc.perform(get("/api/v1/usuarios/5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(5L))
            .andExpect(jsonPath("$.pnombre").value("Laura"));
    }

    @Test
    void testBuscarUsuario_NotFound() throws Exception {
        when(usuarioService.encontrarPorId(99L)).thenThrow(new Exception("Usuario no encontrado"));

        mockMvc.perform(get("/api/v1/usuarios/99"))
            .andExpect(status().isNotFound());
    }

    @Test
    void testActualizarUsuario_OK() throws Exception {
        Usuario usuarioExistente = new Usuario();
        usuarioExistente.setId(1L);
        usuarioExistente.setPnombre("Juan");
        usuarioExistente.setAppaterno("Perez");
        usuarioExistente.setDireccion("Calle Falsa 123");

        Usuario usuarioActualizado = new Usuario();
        usuarioActualizado.setId(1L);
        usuarioActualizado.setPnombre("Juan");
        usuarioActualizado.setAppaterno("Gonzalez");
        usuarioActualizado.setDireccion("Calle Falsa 123");

        // Mock para encontrar el usuario existente
        when(usuarioService.encontrarPorId(1L)).thenReturn(usuarioExistente);
        // Mock para guardar el usuario actualizado
        when(usuarioService.guardarUsuario(any(Usuario.class))).thenReturn(usuarioActualizado);

        Usuario datosParaActualizar = new Usuario();
        datosParaActualizar.setApmaterno("Gonzalez");  // Cambio solo este campo

        mockMvc.perform(put("/api/v1/usuarios/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(datosParaActualizar)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.apmaterno").value("Gonzalez"))
            .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testActualizarUsuario_NotFound() throws Exception {
        when(usuarioService.encontrarPorId(99L)).thenThrow(new Exception("Usuario no encontrado"));

        Usuario datos = new Usuario();
        datos.setApmaterno("Cualquier");

        mockMvc.perform(put("/api/v1/usuarios/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(datos)))
            .andExpect(status().isNotFound());
    }

    @Test
    void testActualizarUsuario_BadRequest() throws Exception {
        Usuario usuarioExistente = new Usuario();
        usuarioExistente.setId(1L);
        usuarioExistente.setPnombre("Juan");
        usuarioExistente.setAppaterno("Perez");
        usuarioExistente.setDireccion("Calle Falsa 123");

        when(usuarioService.encontrarPorId(1L)).thenReturn(usuarioExistente);
        when(usuarioService.guardarUsuario(any(Usuario.class))).thenThrow(new Exception("Error"));

        Usuario datosParaActualizar = new Usuario();
        datosParaActualizar.setApmaterno("Error");

        mockMvc.perform(put("/api/v1/usuarios/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(datosParaActualizar)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testEliminarUsuario_NoContent() throws Exception {
        doNothing().when(usuarioService).borrarUsuario(1L);

        mockMvc.perform(delete("/api/v1/usuarios/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    void testEliminarUsuario_NotFound() throws Exception {
        doThrow(new Exception("Usuario no encontrado")).when(usuarioService).borrarUsuario(99L);

        mockMvc.perform(delete("/api/v1/usuarios/99"))
            .andExpect(status().isNotFound());
    }

}
