package madstodolist.controller;

import java.util.Arrays;

import madstodolist.dto.UsuarioData;
import madstodolist.service.UsuarioService;
import madstodolist.authentication.ManagerUserSession;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.mock.web.MockHttpSession;

import java.util.Collections;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UsuarioWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private ManagerUserSession managerUserSession;

    // TEST LOGIN
    @Test
    public void servicioLoginUsuarioOK() throws Exception {
        UsuarioData anaGarcia = new UsuarioData();
        anaGarcia.setNombre("Ana García");
        anaGarcia.setId(1L);

        when(usuarioService.login("ana.garcia@gmail.com", "12345678"))
                .thenReturn(UsuarioService.LoginStatus.LOGIN_OK);
        when(usuarioService.findByEmail("ana.garcia@gmail.com"))
                .thenReturn(anaGarcia);

        this.mockMvc.perform(post("/login")
                        .param("eMail", "ana.garcia@gmail.com")
                        .param("password", "12345678"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/usuarios/1/tareas"));
    }

    @Test
    public void servicioLoginUsuarioNotFound() throws Exception {
        when(usuarioService.login("pepito.perez@gmail.com", "12345678"))
                .thenReturn(UsuarioService.LoginStatus.USER_NOT_FOUND);

        this.mockMvc.perform(post("/login")
                        .param("eMail", "pepito.perez@gmail.com")
                        .param("password", "12345678"))
                .andExpect(content().string(containsString("No existe usuario")));
    }

    @Test
    public void servicioLoginUsuarioErrorPassword() throws Exception {
        when(usuarioService.login("ana.garcia@gmail.com", "000"))
                .thenReturn(UsuarioService.LoginStatus.ERROR_PASSWORD);

        this.mockMvc.perform(post("/login")
                        .param("eMail", "ana.garcia@gmail.com")
                        .param("password", "000"))
                .andExpect(content().string(containsString("Contraseña incorrecta")));
    }

    @Test
    public void loginMuestraMenuUsuarioCorrectamente() throws Exception {
        UsuarioData usuario = new UsuarioData();
        usuario.setNombre("Ana García");
        usuario.setId(1L);

        when(usuarioService.login("ana.garcia@gmail.com", "12345678"))
                .thenReturn(UsuarioService.LoginStatus.LOGIN_OK);
        when(usuarioService.findByEmail("ana.garcia@gmail.com"))
                .thenReturn(usuario);
        when(usuarioService.findById(1L)).thenReturn(usuario);

        when(managerUserSession.usuarioLogeado()).thenReturn(1L);

        MockHttpSession session = new MockHttpSession();
        this.mockMvc.perform(post("/login")
                        .param("eMail", "ana.garcia@gmail.com")
                        .param("password", "12345678")
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/usuarios/1/tareas"));

        this.mockMvc.perform(get("/usuarios/1/tareas").session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Ana García")))
                .andExpect(content().string(containsString("Cerrar sesión")));
    }

    // TEST LISTA DE USUARIOS
    @Test
    public void getListaUsuariosRegistradosDevuelveOK() throws Exception {
        when(managerUserSession.usuarioLogeado()).thenReturn(1L);
        when(managerUserSession.esAdministrador()).thenReturn(true);
        when(usuarioService.findAllUsuarios()).thenReturn(Collections.emptyList());

        this.mockMvc.perform(get("/registrados"))
                .andExpect(status().isOk())
                .andExpect(view().name("listaUsuariosRegistrados"))
                .andExpect(content().string(containsString("Usuarios Registrados")));
    }

    @Test
    public void getListaUsuariosRegistradosMuestraUsuarios() throws Exception {
        UsuarioData usuario1 = new UsuarioData();
        usuario1.setId(1L);
        usuario1.setEmail("ana.garcia@gmail.com");

        UsuarioData usuario2 = new UsuarioData();
        usuario2.setId(2L);
        usuario2.setEmail("juan.perez@gmail.com");

        when(managerUserSession.usuarioLogeado()).thenReturn(1L);
        when(managerUserSession.esAdministrador()).thenReturn(true);
        when(usuarioService.findAllUsuarios()).thenReturn(Arrays.asList(usuario1, usuario2));

        this.mockMvc.perform(get("/registrados"))
                .andExpect(status().isOk())
                .andExpect(view().name("listaUsuariosRegistrados"))
                .andExpect(content().string(containsString("ana.garcia@gmail.com")))
                .andExpect(content().string(containsString("juan.perez@gmail.com")));
    }

    @Test
    public void getDescripcionUsuarioDevuelveUsuarioCorrecto() throws Exception {
        UsuarioData usuario = new UsuarioData();
        usuario.setId(1L);
        usuario.setEmail("ana.garcia@gmail.com");
        usuario.setNombre("Ana García");

        when(managerUserSession.usuarioLogeado()).thenReturn(1L);
        when(managerUserSession.esAdministrador()).thenReturn(true);
        when(usuarioService.findById(1L)).thenReturn(usuario);

        this.mockMvc.perform(get("/registrados/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("usuarioDescripcion"))
                .andExpect(content().string(containsString("Ana García")))
                .andExpect(content().string(containsString("ana.garcia@gmail.com")));
    }

    @Test
    public void getDescripcionUsuarioNoEncontrado() throws Exception {
        when(managerUserSession.usuarioLogeado()).thenReturn(1L);
        when(managerUserSession.esAdministrador()).thenReturn(true);
        when(usuarioService.findById(1L)).thenReturn(null);

        this.mockMvc.perform(get("/registrados/1"))
                .andExpect(status().isNotFound());
    }

    // TESTS DE PROTECCIÓN DE ACCESO
    @Test
    public void accesoListadoUsuariosRedirigeLoginCuandoNoEstaLogueado() throws Exception {
        when(managerUserSession.usuarioLogeado()).thenReturn(null);

        this.mockMvc.perform(get("/registrados"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    public void accesoListadoUsuariosProhibidoParaNoAdministradores() throws Exception {
        Long usuarioId = 1L;
        UsuarioData usuarioNoAdmin = new UsuarioData();
        usuarioNoAdmin.setId(usuarioId);
        usuarioNoAdmin.setNombre("Juan");
        usuarioNoAdmin.setAdministrador(false);

        when(managerUserSession.usuarioLogeado()).thenReturn(usuarioId);
        when(managerUserSession.esAdministrador()).thenReturn(false);
        when(usuarioService.findById(usuarioId)).thenReturn(usuarioNoAdmin);

        this.mockMvc.perform(get("/registrados"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void administradorBloqueaUsuario() throws Exception {
        Long adminId = 1L;
        Long usuarioId = 2L;
        UsuarioData admin = new UsuarioData();
        admin.setId(adminId);
        admin.setAdministrador(true);

        when(managerUserSession.usuarioLogeado()).thenReturn(adminId);
        when(managerUserSession.esAdministrador()).thenReturn(true);
        when(usuarioService.findById(adminId)).thenReturn(admin);

        this.mockMvc.perform(post("/registrados/" + usuarioId + "/bloquear")
                        .param("accion", "bloquear"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/registrados"));

        verify(usuarioService).cambiarEstadoBloqueoUsuario(usuarioId, true);
    }


    // TEST DE LOGIN DE USUARIO BLOQUEADO
    @Test
    public void usuarioBloqueadoIntentaLogin() throws Exception {
        String email = "usuario.bloqueado@gmail.com";
        String password = "password";

        when(usuarioService.login(email, password))
                .thenReturn(UsuarioService.LoginStatus.USER_BLOCKED);

        this.mockMvc.perform(post("/login")
                        .param("eMail", email)
                        .param("password", password))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Usuario bloqueado. Contacte con el administrador.")));
    }
}
