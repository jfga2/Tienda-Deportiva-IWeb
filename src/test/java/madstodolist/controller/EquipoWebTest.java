package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.dto.EquipoData;
import madstodolist.dto.UsuarioData;
import madstodolist.service.EquipoService;
import madstodolist.service.EquipoServiceException;
import madstodolist.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class EquipoWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ManagerUserSession managerUserSession;

    @MockBean
    private EquipoService equipoService;

    @MockBean
    private UsuarioService usuarioService;

    private EquipoData equipo1;
    private EquipoData equipo2;

    @BeforeEach
    public void setup() {
        equipo1 = new EquipoData();
        equipo1.setId(1L);
        equipo1.setNombre("Proyecto A");

        equipo2 = new EquipoData();
        equipo2.setId(2L);
        equipo2.setNombre("Proyecto B");
    }


    @Test
    public void testMostrarListadoEquipos() throws Exception {
        // Simulamos que hay un usuario logueado y que es administrador
        UsuarioData usuario = new UsuarioData();
        usuario.setId(1L);
        usuario.setNombre("Usuario Test");
        when(managerUserSession.usuarioLogeado()).thenReturn(1L);
        when(managerUserSession.esAdministrador()).thenReturn(true);
        when(usuarioService.findById(1L)).thenReturn(usuario);

        // Simulamos la lista de equipos
        List<EquipoData> equipos = Arrays.asList(equipo1, equipo2);
        when(equipoService.findAllOrdenadoPorNombre()).thenReturn(equipos);

        // Ejecutamos la solicitud y comprobamos la respuesta
        mockMvc.perform(get("/equipos"))
                .andExpect(status().isOk())
                .andExpect(view().name("ListaEquipos"))
                .andExpect(model().attribute("equipos", hasSize(2)))
                .andExpect(content().string(containsString("Proyecto A")))
                .andExpect(content().string(containsString("Proyecto B")));
    }



    @Test
    public void testMostrarUsuariosDeEquipo() throws Exception {
        // Configuramos el mock del equipo y sus usuarios
        when(equipoService.recuperarEquipo(1L)).thenReturn(equipo1);
        when(equipoService.usuariosEquipo(1L)).thenReturn(Collections.emptyList());

        // Simulamos un usuario logeado
        UsuarioData usuarioLogeado = new UsuarioData();
        usuarioLogeado.setId(1L);
        usuarioLogeado.setNombre("Usuario Prueba");
        when(managerUserSession.usuarioLogeado()).thenReturn(1L);
        when(usuarioService.findById(1L)).thenReturn(usuarioLogeado);

        // Ejecutamos la solicitud GET a la ruta de los usuarios del equipo y realizamos las verificaciones
        mockMvc.perform(get("/equipos/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("usuariosEquipo"))
                .andExpect(model().attribute("equipo", equipo1))
                .andExpect(model().attribute("usuarios", hasSize(0)))
                .andExpect(content().string(containsString(equipo1.getNombre()))) // Comprobamos solo que aparezca el nombre del equipo
                .andExpect(content().string(containsString("Usuario Prueba")));   // Verifica que el nombre del usuario logeado esté presente
    }

    @Test
    public void testCrearEquipoExitosamente() throws Exception {
        mockMvc.perform(post("/equipos/crear")
                        .param("nombre", "Nuevo Equipo")
                        .param("descripcion", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/equipos"))
                .andExpect(flash().attribute("mensaje", "Equipo creado correctamente"));
    }

    @Test
    public void testUnirseEquipoExitosamente() throws Exception {
        // Configuramos el mock para usuario logeado
        when(managerUserSession.usuarioLogeado()).thenReturn(1L);

        mockMvc.perform(post("/equipos/1/unirse"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/equipos"))
                .andExpect(flash().attribute("mensaje", "Te has unido al equipo"));
    }

    @Test
    public void testSalirDeEquipoExitosamente() throws Exception {
        // Configuramos el mock para usuario logeado
        when(managerUserSession.usuarioLogeado()).thenReturn(1L);

        mockMvc.perform(post("/equipos/1/salir"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/equipos"))
                .andExpect(flash().attribute("mensaje", "Has salido del equipo"));
    }

    @Test
    public void testActualizarNombreEquipo() throws Exception {
        mockMvc.perform(post("/equipos/1/actualizarNombre")
                        .param("nuevoNombre", "Nuevo Nombre del Equipo"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/equipos"))
                .andExpect(flash().attributeExists("mensaje"));
    }

    @Test
    public void testEliminarEquipo_Success() throws Exception {
        // Ejecutamos el POST para eliminar un equipo con ID 1
        mockMvc.perform(post("/equipos/1/eliminar"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/equipos"))
                .andExpect(flash().attribute("mensaje", "El equipo se ha eliminado correctamente"));

        // Verificamos que el servicio de eliminación fue llamado correctamente
        verify(equipoService).eliminarEquipo(1L);
    }

    @Test
    public void testEliminarEquipo_Failure() throws Exception {
        // Configuramos el servicio para lanzar una excepción cuando se intente eliminar
        doThrow(new EquipoServiceException("Error eliminando el equipo")).when(equipoService).eliminarEquipo(1L);

        // Ejecutamos el POST para eliminar el equipo y comprobamos que se maneja el error
        mockMvc.perform(post("/equipos/1/eliminar"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/equipos"))
                .andExpect(flash().attribute("error", "Error eliminando el equipo"));
    }

    @Test
    public void testCrearEquipoDescripcionExitosamente() throws Exception {

        equipoService.crearEquipoDescripcion("Nuevo equipo", "Nueva descripcion");

        EquipoData equipo3 = new EquipoData();
        equipo3.setId(3L);
        equipo3.setNombre("Proyecto A");
        equipo3.setDescripcion("Descripcion equipo");

        // Simulamos la lista de equipos
        List<EquipoData> equipos = Arrays.asList(equipo3);
        when(equipoService.findAllOrdenadoPorNombre()).thenReturn(equipos);

        this.mockMvc.perform(get("/equipos"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Descripcion equipo")));
    }



}
