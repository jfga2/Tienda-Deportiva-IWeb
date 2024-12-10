package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.dto.TareaData;
import madstodolist.dto.UsuarioData;
import madstodolist.service.TareaService;
import madstodolist.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/clean-db.sql")
public class TareaWebTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TareaService tareaService;

    @Autowired
    private UsuarioService usuarioService;

    @MockBean
    private ManagerUserSession managerUserSession;

    Map<String, Long> addUsuarioTareasBD() {
        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("user@ua");
        usuario.setPassword("123");
        usuario.setNombre("Younes");  // Aseguramos que el nombre está presente
        usuario = usuarioService.registrar(usuario);

        // Añadimos más de 6 tareas para probar la paginación
        tareaService.nuevaTareaUsuario(usuario.getId(), "Tarea 1");
        tareaService.nuevaTareaUsuario(usuario.getId(), "Tarea 2");
        tareaService.nuevaTareaUsuario(usuario.getId(), "Tarea 3");
        tareaService.nuevaTareaUsuario(usuario.getId(), "Tarea 4");
        tareaService.nuevaTareaUsuario(usuario.getId(), "Tarea 5");
        tareaService.nuevaTareaUsuario(usuario.getId(), "Tarea 6");
        tareaService.nuevaTareaUsuario(usuario.getId(), "Tarea 7");

        Map<String, Long> ids = new HashMap<>();
        ids.put("usuarioId", usuario.getId());
        return ids;
    }

    @Test
    public void listaTareas() throws Exception {
        Long usuarioId = addUsuarioTareasBD().get("usuarioId");

        when(managerUserSession.usuarioLogeado()).thenReturn(usuarioId);

        String url = "/usuarios/" + usuarioId + "/tareas";

        this.mockMvc.perform(get(url))
                .andExpect(content().string(allOf(
                        containsString("Tarea 1"),
                        containsString("Tarea 2"),
                        containsString("Tarea 3"),
                        containsString("Tarea 4"),
                        containsString("Tarea 5"),
                        containsString("Tarea 6"),
                        not(containsString("Tarea 7")) // La séptima tarea está en la página siguiente
                )));
    }

    @Test
    public void getNuevaTareaDevuelveForm() throws Exception {
        Long usuarioId = addUsuarioTareasBD().get("usuarioId");

        when(managerUserSession.usuarioLogeado()).thenReturn(usuarioId);

        String urlPeticion = "/usuarios/" + usuarioId + "/tareas/nueva";
        String urlAction = "action=\"/usuarios/" + usuarioId + "/tareas/nueva\"";

        this.mockMvc.perform(get(urlPeticion))
                .andExpect(content().string(allOf(
                        containsString("form method=\"post\""),
                        containsString(urlAction)
                )));
    }


    @Test
    public void deleteTareaDevuelveOKyBorraTarea() throws Exception {
        Map<String, Long> ids = addUsuarioTareasBD();
        Long usuarioId = ids.get("usuarioId");
        Long tareaId = tareaService.allTareasUsuario(usuarioId).get(0).getId(); // Obtén la primera tarea

        when(managerUserSession.usuarioLogeado()).thenReturn(usuarioId);

        String urlDelete = "/tareas/" + tareaId;

        this.mockMvc.perform(delete(urlDelete))
                .andExpect(status().isOk());

        String urlListado = "/usuarios/" + usuarioId + "/tareas";

        this.mockMvc.perform(get(urlListado))
                .andExpect(content().string(not(containsString("Tarea 1")))); // Ajusta el nombre según los datos creados
    }


    @Test
    public void editarTareaActualizaLaTarea() throws Exception {
        Map<String, Long> ids = addUsuarioTareasBD();
        Long usuarioId = ids.get("usuarioId");
        Long tareaId = tareaService.allTareasUsuario(usuarioId).get(0).getId(); // Obtén la primera tarea

        when(managerUserSession.usuarioLogeado()).thenReturn(usuarioId);

        String urlEditar = "/tareas/" + tareaId + "/editar";
        String urlRedirect = "/usuarios/" + usuarioId + "/tareas";

        this.mockMvc.perform(post(urlEditar)
                        .param("titulo", "Limpiar cristales coche"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(urlRedirect));

        String urlListado = "/usuarios/" + usuarioId + "/tareas";

        this.mockMvc.perform(get(urlListado))
                .andExpect(content().string(containsString("Limpiar cristales coche")));
    }


    @Test
    public void getListaTareasMuestraMenuUsuarioCuandoEstaLogueado() throws Exception {
        Long usuarioId = addUsuarioTareasBD().get("usuarioId");

        when(managerUserSession.usuarioLogeado()).thenReturn(usuarioId);

        String url = "/usuarios/" + usuarioId + "/tareas";

        this.mockMvc.perform(get(url))
                .andExpect(content().string(containsString("Tareas")))
                .andExpect(content().string(containsString("Younes")))  // Asegura que el nombre es correcto
                .andExpect(content().string(containsString("Cerrar sesión")));
    }

    @Test
    public void getListaTareasMuestraLoginRegistroCuandoNoEstaLogueado() throws Exception {
        when(managerUserSession.usuarioLogeado()).thenReturn(null);

        String url = "/usuarios/1/tareas";

        this.mockMvc.perform(get(url))
                .andExpect(status().isFound()) // Redirige a /login
                .andExpect(redirectedUrl("/login"));
    }

    // Nuevo test 1: Primera página de la lista de tareas
    @Test
    public void testListaTareasPrimeraPagina() throws Exception {
        Long usuarioId = addUsuarioTareasBD().get("usuarioId");

        when(managerUserSession.usuarioLogeado()).thenReturn(usuarioId);

        String url = "/usuarios/" + usuarioId + "/tareas?page=0&size=6";

        this.mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(content().string(allOf(
                        containsString("Tarea 1"),
                        containsString("Tarea 6"),
                        not(containsString("Tarea 7"))
                )));
    }

    // Nuevo test 2: Segunda página de la lista de tareas
    @Test
    public void testListaTareasSegundaPagina() throws Exception {
        Long usuarioId = addUsuarioTareasBD().get("usuarioId");

        when(managerUserSession.usuarioLogeado()).thenReturn(usuarioId);

        String url = "/usuarios/" + usuarioId + "/tareas?page=1&size=6";

        this.mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(content().string(allOf(
                        containsString("Tarea 7"),
                        not(containsString("Tarea 1")),
                        not(containsString("Tarea 6"))
                )));
    }
}
