package madstodolist.controller;

import madstodolist.dto.UsuarioData;
import madstodolist.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.jdbc.Sql;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/clean-db.sql")
public class BarraMenuWebTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioService usuarioService;

    private Long usuarioId;

    @BeforeEach
    public void setUp() {
        // Aseguramos que el usuario esté registrado antes de la prueba
        usuarioId = addUsuarioBD();
    }

    // Método para registrar un usuario en la base de datos
    Long addUsuarioBD() {
        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("test@example.com");
        usuario.setNombre("Usuario Ejemplo");
        usuario.setPassword("password");
        UsuarioData nuevoUsuario = usuarioService.registrar(usuario);
        return nuevoUsuario.getId();
    }

    @Test
    public void barraMenuParaUsuarioNoLogeado() throws Exception {
        // Verifica que la barra de menú para usuarios no logeados tiene enlaces a Login y Registro
        mockMvc.perform(get("/about"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Login")))
                .andExpect(content().string(containsString("Registro")));
    }

    @Test
    public void barraMenuParaUsuarioLogeado() throws Exception {
        // Simulamos que el usuario está logeado estableciendo el idUsuarioLogeado en la sesión
        mockMvc.perform(get("/usuarios/" + usuarioId + "/tareas")
                        .sessionAttr("idUsuarioLogeado", usuarioId)
                        .sessionAttr("nombreUsuario", "Usuario Ejemplo"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Tareas")))
                .andExpect(content().string(containsString("Cuenta")))
                .andExpect(content().string(containsString("Cerrar sesión")));
    }

}
