package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.dto.UsuarioData;
import madstodolist.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
public class AcercaDeWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ManagerUserSession managerUserSession;

    @MockBean
    private UsuarioService usuarioService;

    @Test
    public void getAboutDevuelveNombreAplicacion() throws Exception {
        // GIVEN
        // Simulamos que un usuario está logueado
        Long usuarioId = 1L;
        UsuarioData usuario = new UsuarioData();
        usuario.setId(usuarioId);
        usuario.setNombre("Ana García");

        when(managerUserSession.usuarioLogeado()).thenReturn(usuarioId);
        when(usuarioService.findById(usuarioId)).thenReturn(usuario);

        // WHEN, THEN
        // Realizamos la petición y verificamos que aparece "ToDoList" en la respuesta
        this.mockMvc.perform(get("/about"))
                .andExpect(content().string(containsString("ToDoList")));
    }

    @Test
    public void getAboutMuestraLoginRegistroCuandoNoEstaLogueado() throws Exception {
        // Simulamos que no hay usuario logueado
        when(managerUserSession.usuarioLogeado()).thenReturn(null);

        this.mockMvc.perform(get("/about"))
                .andExpect(content().string(containsString("Login")))
                .andExpect(content().string(containsString("Registro")));
    }

    @Test
    public void getAboutMuestraMenuUsuarioCuandoEstaLogueado() throws Exception {
        // Simulamos un usuario logueado
        Long usuarioId = 1L;
        UsuarioData usuario = new UsuarioData();
        usuario.setId(usuarioId);
        usuario.setNombre("Younes");

        when(managerUserSession.usuarioLogeado()).thenReturn(usuarioId);
        when(usuarioService.findById(usuarioId)).thenReturn(usuario);

        this.mockMvc.perform(get("/about"))
                .andExpect(content().string(containsString("Tareas")))
                .andExpect(content().string(containsString("Younes")))
                .andExpect(content().string(containsString("Cerrar sesión")));
    }
}
