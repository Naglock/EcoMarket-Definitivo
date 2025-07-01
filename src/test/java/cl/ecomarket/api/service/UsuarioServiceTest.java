package cl.ecomarket.api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import cl.ecomarket.api.model.Usuario;
import cl.ecomarket.api.repository.UsuarioRepository;
import cl.ecomarket.api.services.UsuarioService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void testListarUsuarios() {
        Usuario u1 = new Usuario();
        Usuario u2 = new Usuario();
        when(usuarioRepository.findAll()).thenReturn(Arrays.asList(u1, u2));

        List<Usuario> resultado = usuarioService.listarUsuarios();

        assertEquals(2, resultado.size());
        verify(usuarioRepository).findAll();
    }

    @Test
    void testEncontrarPorId_Existe() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        when(usuarioRepository.existsById(1L)).thenReturn(true);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        Usuario resultado = usuarioService.encontrarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }

    @Test
    void testEncontrarPorId_NoExiste() {
        when(usuarioRepository.existsById(1L)).thenReturn(false);
        // No mockeamos findById porque no se llama cuando existsById devuelve false

        Exception ex = assertThrows(Exception.class, () -> usuarioService.encontrarPorId(1L));
        assertEquals("Usuario no encontrado", ex.getMessage());
    }

    @Test
    void testGuardarUsuario_Exitoso() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setPnombre("Juan");
        usuario.setAppaterno("Perez");
        usuario.setDireccion("Calle Falsa 123");

        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        Usuario resultado = usuarioService.guardarUsuario(usuario);

        assertNotNull(resultado);
        assertEquals("Juan", resultado.getPnombre());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void testGuardarUsuario_FaltaPnombre() {
        Usuario usuario = new Usuario();
        usuario.setPnombre(""); // vacío
        usuario.setAppaterno("Perez");
        usuario.setDireccion("Calle Falsa 123");

        Exception ex = assertThrows(Exception.class, () -> usuarioService.guardarUsuario(usuario));
        assertEquals("El primer nombre del usuario no puede estar vacío", ex.getMessage());
    }

    @Test
    void testGuardarUsuario_FaltaAppaterno() {
        Usuario usuario = new Usuario();
        usuario.setPnombre("Juan");
        usuario.setAppaterno(""); // vacío
        usuario.setDireccion("Calle Falsa 123");

        Exception ex = assertThrows(Exception.class, () -> usuarioService.guardarUsuario(usuario));
        assertEquals("El apellido paterno del usuario no puede estar vacío", ex.getMessage());
    }

    @Test
    void testGuardarUsuario_FaltaDireccion() {
        Usuario usuario = new Usuario();
        usuario.setPnombre("Juan");
        usuario.setAppaterno("Perez");
        usuario.setDireccion(""); // vacío

        Exception ex = assertThrows(Exception.class, () -> usuarioService.guardarUsuario(usuario));
        assertEquals("La dirección del usuario no puede estar vacía", ex.getMessage());
    }

    @Test
    void testBorrarUsuario_Existe() throws Exception {
        when(usuarioRepository.existsById(1L)).thenReturn(true);

        usuarioService.borrarUsuario(1L);

        verify(usuarioRepository).deleteById(1L);
    }

    @Test
    void testBorrarUsuario_NoExiste() {
        when(usuarioRepository.existsById(1L)).thenReturn(false);

        Exception ex = assertThrows(Exception.class, () -> usuarioService.borrarUsuario(1L));
        assertEquals("Usuario no encontrado", ex.getMessage());
    }
}
