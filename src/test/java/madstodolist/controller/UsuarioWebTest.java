package madstodolist.controller;

import madstodolist.dto.UsuarioData;
import madstodolist.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
//
// A diferencia de los tests web de tarea, donde usábamos los datos
// de prueba de la base de datos, aquí vamos a practicar otro enfoque:
// moquear el usuarioService.
public class UsuarioWebTest {

    @Autowired
    private MockMvc mockMvc;

    // Moqueamos el usuarioService.
    // En los tests deberemos proporcionar el valor devuelto por las llamadas
    // a los métodos de usuarioService que se van a ejecutar cuando se realicen
    // las peticiones a los endpoint.
    @MockBean
    private UsuarioService usuarioService;

    @Test
    public void servicioLoginUsuarioOK() throws Exception {
        // GIVEN
        // Moqueamos la llamada a usuarioService.login para que
        // devuelva un LOGIN_OK y la llamada a usuarioServicie.findByEmail
        // para que devuelva un usuario determinado.

        UsuarioData anaGarcia = new UsuarioData();
        anaGarcia.setNombre("Ana García");
        anaGarcia.setId(1L);

        when(usuarioService.login("ana.garcia@gmail.com", "12345678"))
                .thenReturn(UsuarioService.LoginStatus.LOGIN_OK);
        when(usuarioService.findByEmail("ana.garcia@gmail.com"))
                .thenReturn(anaGarcia);

        // WHEN, THEN
        // Realizamos una petición POST al login pasando los datos
        // esperados en el mock, la petición devolverá una redirección a la
        // URL con las tareas del usuario

        this.mockMvc.perform(post("/login")
                        .param("eMail", "ana.garcia@gmail.com")
                        .param("password", "12345678"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/usuarios/1/tareas"));
    }

    @Test
    public void servicioLoginUsuarioNotFound() throws Exception {
        // GIVEN
        // Moqueamos el método usuarioService.login para que devuelva
        // USER_NOT_FOUND
        when(usuarioService.login("pepito.perez@gmail.com", "12345678"))
                .thenReturn(UsuarioService.LoginStatus.USER_NOT_FOUND);

        // WHEN, THEN
        // Realizamos una petición POST con los datos del usuario mockeado y
        // se debe devolver una página que contenga el mensaja "No existe usuario"
        this.mockMvc.perform(post("/login")
                        .param("eMail","pepito.perez@gmail.com")
                        .param("password","12345678"))
                .andExpect(content().string(containsString("No existe usuario")));
    }

    @Test
    public void servicioLoginUsuarioErrorPassword() throws Exception {
        // GIVEN
        // Moqueamos el método usuarioService.login para que devuelva
        // ERROR_PASSWORD
        when(usuarioService.login("ana.garcia@gmail.com", "000"))
                .thenReturn(UsuarioService.LoginStatus.ERROR_PASSWORD);

        // WHEN, THEN
        // Realizamos una petición POST con los datos del usuario mockeado y
        // se debe devolver una página que contenga el mensaja "Contraseña incorrecta"
        this.mockMvc.perform(post("/login")
                        .param("eMail","ana.garcia@gmail.com")
                        .param("password","000"))
                .andExpect(content().string(containsString("Contraseña incorrecta")));
    }

    @Test
    public void redirigirAdminALaListaDeUsuariosAlIniciarSesion() throws Exception {
        // GIVEN
        UsuarioData usuarioAdmin = new UsuarioData();
        usuarioAdmin.setId(1L);
        usuarioAdmin.setNombre("Admin Ejemplo");
        usuarioAdmin.setAdmin(true);


        // Moqueamos el servicio para simular un inicio de sesión exitoso como administrador
        when(usuarioService.login("admin@ua.com", "12345")).thenReturn(UsuarioService.LoginStatus.LOGIN_OK);
        when(usuarioService.findByEmail("admin@ua.com")).thenReturn(usuarioAdmin);

        // WHEN, THEN
        this.mockMvc.perform(post("/login")
                        .param("eMail", "admin@ua.com")
                        .param("password", "12345"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/registrados"));
    }

    @Test
    public void noMostrarCheckboxAdminSiYaExisteAdmin() throws Exception {
        // GIVEN
        when(usuarioService.existsAdmin()).thenReturn(true);

        // WHEN, THEN
        this.mockMvc.perform(get("/registro"))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("Registrar como administrador"))));
    }

    @Test
    public void accesoAListaUsuariosConPermisoDebeFuncionar() throws Exception {
        // GIVEN
        // Usuario administrador en sesión
        UsuarioData usuarioAdmin = new UsuarioData();
        usuarioAdmin.setId(1L);
        usuarioAdmin.setAdmin(true);
        when(usuarioService.findById(1L)).thenReturn(usuarioAdmin);

        // WHEN, THEN
        this.mockMvc.perform(get("/registrados").sessionAttr("idUsuarioLogeado", 1L))
                .andExpect(status().isOk());
    }

    @Test
    public void accesoAListaUsuariosSinPermisoDebeFallar() throws Exception {
        // GIVEN
        // Usuario no administrador en sesión
        UsuarioData usuarioNoAdmin = new UsuarioData();
        usuarioNoAdmin.setId(2L);
        usuarioNoAdmin.setAdmin(false);
        when(usuarioService.findById(2L)).thenReturn(usuarioNoAdmin);

        // WHEN, THEN
        this.mockMvc.perform(get("/registrados").sessionAttr("idUsuarioLogeado", 2L))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void loginUsuarioBloqueadoDevuelveError() throws Exception {
        // GIVEN
        when(usuarioService.login("user@ua", "123")).thenReturn(UsuarioService.LoginStatus.LOGIN_BLOCKED);

        // WHEN, THEN
        this.mockMvc.perform(post("/login")
                        .param("eMail", "user@ua")
                        .param("password", "123"))
                .andExpect(content().string(containsString("Tu cuenta está bloqueada. Contacta con el administrador.")));
    }


}
