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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;  

@SpringBootTest
@AutoConfigureMockMvc
public class AdministradorWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private ManagerUserSession managerUserSession;

    // TEST 1: Verificar que un administrador accede a la lista de usuarios registrados
    @Test
    public void administradorAccedeListaUsuarios() throws Exception {
        // Simulamos un usuario administrador logueado
        Long adminId = 1L;
        UsuarioData admin = new UsuarioData();
        admin.setId(adminId);
        admin.setNombre("Admin");

        admin.setAdministrador(true);  // El usuario es administrador

        // Simulamos que el administrador está logueado
        when(managerUserSession.usuarioLogeado()).thenReturn(adminId);
        when(managerUserSession.esAdministrador()).thenReturn(true);
        when(usuarioService.findById(adminId)).thenReturn(admin);

        // Realizamos la petición y verificamos que redirige correctamente a la lista de usuarios
        this.mockMvc.perform(get("/registrados"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Usuarios Registrados")));
    }

    // TEST 2: Verificar que el checkbox de administrador no aparece si ya hay un administrador
    @Test
    public void noMuestraCheckboxAdministradorSiYaExiste() throws Exception {
        // Simulamos que ya existe un administrador en el sistema
        when(usuarioService.existeAdministrador()).thenReturn(true);

        // Realizamos la petición para mostrar el formulario de registro
        this.mockMvc.perform(get("/registro"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Correo electrónico")))
                .andExpect(content().string(containsString("Contraseña")))
                // Verificamos que no aparece el checkbox de administrador usando 'not'
                .andExpect(content().string(not(containsString("type=\"checkbox\""))));
    }

    // TEST 3: Verificar que el checkbox de administrador aparece si NO hay un administrador
    @Test
    public void muestraCheckboxAdministradorSiNoExiste() throws Exception {
        // Simulamos que NO existe un administrador en el sistema
        when(usuarioService.existeAdministrador()).thenReturn(false);

        // Realizamos la petición para mostrar el formulario de registro
        this.mockMvc.perform(get("/registro"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Correo electrónico")))
                .andExpect(content().string(containsString("Contraseña")))
                // Verificamos que sí aparece el checkbox de administrador
                .andExpect(content().string(containsString("type=\"checkbox\"")));
    }


}
