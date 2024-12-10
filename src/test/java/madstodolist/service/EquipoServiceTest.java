package madstodolist.service;

import madstodolist.dto.EquipoData;
import madstodolist.dto.UsuarioData;
import madstodolist.model.Equipo;
import madstodolist.repository.EquipoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import javax.transaction.Transactional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@Sql(scripts = "/clean-db.sql")
public class EquipoServiceTest {

    @Autowired
    EquipoService equipoService;

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    EquipoRepository equipoRepository;

    @Test
    public void crearRecuperarEquipo() {
        // Crear equipo
        EquipoData equipo = equipoService.crearEquipo("Proyecto 1");
        assertThat(equipo.getId()).isNotNull();

        // Recuperar equipo
        EquipoData equipoBd = equipoService.recuperarEquipo(equipo.getId());
        assertThat(equipoBd).isNotNull();
        assertThat(equipoBd.getNombre()).isEqualTo("Proyecto 1");
    }

    @Test
    public void listadoEquiposOrdenAlfabetico() {
        // GIVEN
        // Dos equipos en la base de datos
        equipoService.crearEquipo("Proyecto BBB");
        equipoService.crearEquipo("Proyecto AAA");

        // WHEN
        // Recuperamos los equipos
        List<EquipoData> equipos = equipoService.findAllOrdenadoPorNombre();

        // THEN
        // Los equipos están ordenados por nombre
        assertThat(equipos).hasSize(2);
        assertThat(equipos.get(0).getNombre()).isEqualTo("Proyecto AAA");
        assertThat(equipos.get(1).getNombre()).isEqualTo("Proyecto BBB");
    }

    @Test
    public void añadirUsuarioAEquipo() {
        // GIVEN
        // Un usuario y un equipo en la base de datos
        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("user@ua");
        usuario.setPassword("123");
        usuario = usuarioService.registrar(usuario);
        EquipoData equipo = equipoService.crearEquipo("Proyecto 1");

        // WHEN
        // Añadimos el usuario al equipo
        equipoService.añadirUsuarioAEquipo(equipo.getId(), usuario.getId());

        // THEN
        // El usuario pertenece al equipo
        List<UsuarioData> usuarios = equipoService.usuariosEquipo(equipo.getId());
        assertThat(usuarios).hasSize(1);
        assertThat(usuarios.get(0).getEmail()).isEqualTo("user@ua");
    }

    @Test
    public void recuperarEquiposDeUsuario() {
        // GIVEN
        // Un usuario y dos equipos en la base de datos
        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("user@ua");
        usuario.setPassword("123");
        usuario = usuarioService.registrar(usuario);

        EquipoData equipo1 = equipoService.crearEquipo("Proyecto 1");
        EquipoData equipo2 = equipoService.crearEquipo("Proyecto 2");

        equipoService.añadirUsuarioAEquipo(equipo1.getId(), usuario.getId());
        equipoService.añadirUsuarioAEquipo(equipo2.getId(), usuario.getId());

        // WHEN
        // Recuperamos los equipos del usuario
        List<EquipoData> equipos = equipoService.equiposUsuario(usuario.getId());

        // THEN
        // Verificamos que el usuario pertenece a ambos equipos
        assertThat(equipos).hasSize(2);
        assertThat(equipos.get(0).getNombre()).isEqualTo("Proyecto 1");
        assertThat(equipos.get(1).getNombre()).isEqualTo("Proyecto 2");
    }

    @Test
    public void comprobarExcepciones() {
        // Comprobamos las excepciones lanzadas por los métodos
        assertThatThrownBy(() -> equipoService.recuperarEquipo(1L))
                .isInstanceOf(EquipoServiceException.class);

        assertThatThrownBy(() -> equipoService.añadirUsuarioAEquipo(1L, 1L))
                .isInstanceOf(EquipoServiceException.class);

        assertThatThrownBy(() -> equipoService.usuariosEquipo(1L))
                .isInstanceOf(EquipoServiceException.class);

        assertThatThrownBy(() -> equipoService.equiposUsuario(1L))
                .isInstanceOf(EquipoServiceException.class);

        // Creamos un equipo pero no un usuario y comprobamos que también se lanza una excepción
        EquipoData equipo = equipoService.crearEquipo("Proyecto 1");
        assertThatThrownBy(() -> equipoService.añadirUsuarioAEquipo(equipo.getId(), 1L))
                .isInstanceOf(EquipoServiceException.class);
    }

    @Test
    @Transactional
    public void eliminarUsuarioDeEquipo() {
        // GIVEN
        // Un equipo y un usuario en la base de datos
        EquipoData equipo = equipoService.crearEquipo("Proyecto 1");

        // Creamos y configuramos un UsuarioData con sus propiedades
        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("user@ua.es");
        usuario.setPassword("123");
        usuario.setNombre("User");

        // Registramos el usuario en la base de datos
        usuario = usuarioService.registrar(usuario);

        // Añadimos el usuario al equipo
        equipoService.añadirUsuarioAEquipo(equipo.getId(), usuario.getId());
        assertThat(equipoService.usuariosEquipo(equipo.getId())).contains(usuario);

        // WHEN
        // Eliminamos el usuario del equipo
        equipoService.eliminarUsuarioDeEquipo(equipo.getId(), usuario.getId());

        // THEN
        // El usuario ya no pertenece al equipo
        assertThat(equipoService.usuariosEquipo(equipo.getId())).doesNotContain(usuario);
    }

    @Test
    public void noPermitirNombresVaciosAlCrearEquipo() {
        assertThatThrownBy(() -> equipoService.crearEquipo(""))
                .isInstanceOf(EquipoServiceException.class)
                .hasMessageContaining("El nombre del equipo no puede estar vacío");
    }

    @Test
    public void noPermitirUsuarioDuplicadoEnEquipo() {
        // Creamos el equipo
        EquipoData equipo = equipoService.crearEquipo("Proyecto 1");

        // Creamos y configuramos el usuario
        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("user@ua.es");
        usuario.setPassword("123");
        usuario.setNombre("User");

        // Registramos el usuario y guardamos el resultado en una nueva variable con ID
        UsuarioData usuarioRegistrado = usuarioService.registrar(usuario);  // usuarioRegistrado contiene el ID válido

        // Añadimos el usuario registrado al equipo
        equipoService.añadirUsuarioAEquipo(equipo.getId(), usuarioRegistrado.getId());

        // Intentamos añadir el mismo usuario otra vez y verificamos que lanza una excepción
        assertThatThrownBy(() -> equipoService.añadirUsuarioAEquipo(equipo.getId(), usuarioRegistrado.getId()))
                .isInstanceOf(EquipoServiceException.class)
                .hasMessageContaining("El usuario ya pertenece al equipo");
    }

    @Test
    public void actualizarNombreEquipo() {
        // GIVEN: Un equipo en la base de datos
        Equipo equipo = new Equipo("Proyecto Original");
        equipoRepository.save(equipo);

        // WHEN: Actualizamos el nombre del equipo
        equipoService.actualizarNombreEquipo(equipo.getId(), "Nuevo Nombre");

        // THEN: Verificamos que el nombre se ha actualizado
        Equipo equipoActualizado = equipoRepository.findById(equipo.getId()).orElse(null);
        assertThat(equipoActualizado).isNotNull();
        assertThat(equipoActualizado.getNombre()).isEqualTo("Nuevo Nombre");
    }

    @Test
    public void eliminarEquipo() {
        // GIVEN: Un equipo en la base de datos
        Equipo equipo = new Equipo("Proyecto para eliminar");
        equipoRepository.save(equipo);

        // WHEN: Eliminamos el equipo
        equipoService.eliminarEquipo(equipo.getId());

        // THEN: Verificamos que el equipo ya no existe
        Equipo equipoEliminado = equipoRepository.findById(equipo.getId()).orElse(null);
        assertThat(equipoEliminado).isNull();
    }

    @Test
    public void probarDescripcionEquipo() {
        // Crear equipo
        EquipoData equipo = equipoService.crearEquipo("Proyecto 1");
        equipoService.anyadirDescripcionEquipo(equipo.getId(), "Descripcion equipo proyecto 1");

        // Recuperar equipo
        EquipoData equipoBd = equipoService.recuperarEquipo(equipo.getId());
        assertThat(equipoBd).isNotNull();
        assertThat(equipoBd.getNombre()).isEqualTo("Proyecto 1");
        assertThat(equipoBd.getDescripcion()).isEqualTo("Descripcion equipo proyecto 1");
    }
}
