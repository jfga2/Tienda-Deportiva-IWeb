package madstodolist.service;

import madstodolist.dto.EquipoData;
import madstodolist.dto.UsuarioData;
import madstodolist.model.Equipo;
import madstodolist.model.Usuario;
import madstodolist.repository.EquipoRepository;
import madstodolist.repository.UsuarioRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EquipoService {

    @Autowired
    EquipoRepository equipoRepository;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Transactional
    public EquipoData crearEquipo(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new EquipoServiceException("El nombre del equipo no puede estar vacío");
        }
        Equipo equipo = new Equipo(nombre);
        equipoRepository.save(equipo);
        return modelMapper.map(equipo, EquipoData.class);
    }

    @Transactional
    public EquipoData crearEquipoDescripcion(String nombre, String descripcion) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new EquipoServiceException("El nombre del equipo no puede estar vacío");
        }
        Equipo equipo = new Equipo(nombre);
        equipo.setDescripcion(descripcion);
        equipoRepository.save(equipo);
        return modelMapper.map(equipo, EquipoData.class);
    }

    @Transactional(readOnly = true)
    public EquipoData recuperarEquipo(Long id) {
        Equipo equipo = equipoRepository.findById(id).orElseThrow(
                () -> new EquipoServiceException("Equipo no encontrado con ID: " + id)
        );
        return modelMapper.map(equipo, EquipoData.class);
    }

    @Transactional(readOnly = true)
    public List<EquipoData> findAllOrdenadoPorNombre() {
        return equipoRepository.findAll().stream()
                .sorted((e1, e2) -> e1.getNombre().compareToIgnoreCase(e2.getNombre()))
                .map(equipo -> modelMapper.map(equipo, EquipoData.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public void añadirUsuarioAEquipo(Long equipoId, Long usuarioId) {
        Equipo equipo = equipoRepository.findById(equipoId).orElseThrow(
                () -> new EquipoServiceException("Equipo no encontrado con ID: " + equipoId)
        );
        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow(
                () -> new EquipoServiceException("Usuario no encontrado con ID: " + usuarioId)
        );

        if (equipo.getUsuarios().contains(usuario)) {
            throw new EquipoServiceException("El usuario ya pertenece al equipo");
        }

        equipo.addUsuario(usuario);
        equipoRepository.save(equipo); // Persiste la relación
    }

    @Transactional(readOnly = true)
    public List<UsuarioData> usuariosEquipo(Long equipoId) {
        Equipo equipo = equipoRepository.findById(equipoId).orElseThrow(
                () -> new EquipoServiceException("Equipo no encontrado con ID: " + equipoId)
        );
        return equipo.getUsuarios().stream()
                .map(usuario -> modelMapper.map(usuario, UsuarioData.class))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EquipoData> equiposUsuario(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow(
                () -> new EquipoServiceException("Usuario no encontrado con ID: " + usuarioId)
        );
        return usuario.getEquipos().stream()
                .map(equipo -> modelMapper.map(equipo, EquipoData.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public void eliminarUsuarioDeEquipo(Long equipoId, Long usuarioId) {
        Equipo equipo = equipoRepository.findById(equipoId).orElseThrow(
                () -> new EquipoServiceException("Equipo no encontrado con ID: " + equipoId)
        );
        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow(
                () -> new EquipoServiceException("Usuario no encontrado con ID: " + usuarioId)
        );

        if (!equipo.getUsuarios().contains(usuario)) {
            throw new EquipoServiceException("El usuario no pertenece al equipo.");
        }

        equipo.getUsuarios().remove(usuario);
        usuario.getEquipos().remove(equipo);
        equipoRepository.save(equipo); // actualiza la relación en la base de datos como se piden en el enuncaido
    }

    @Transactional
    public void actualizarNombreEquipo(Long equipoId, String nuevoNombre) {
        Equipo equipo = equipoRepository.findById(equipoId)
                .orElseThrow(() -> new EquipoServiceException("Equipo no encontrado con ID: " + equipoId));
        equipo.setNombre(nuevoNombre);
        equipoRepository.save(equipo);
    }

    @Transactional
    public void eliminarEquipo(Long equipoId) {
        Equipo equipo = equipoRepository.findById(equipoId)
                .orElseThrow(() -> new EquipoServiceException("Equipo no encontrado con ID: " + equipoId));
        equipoRepository.delete(equipo);
    }

    @Transactional(readOnly = true)
    public boolean usuarioPertenece(Long equipoId, Long usuarioId) {
        Equipo equipo = equipoRepository.findById(equipoId).orElseThrow(
                () -> new EquipoServiceException("Equipo no encontrado con ID: " + equipoId)
        );
        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow(
                () -> new EquipoServiceException("Usuario no encontrado con ID: " + usuarioId)
        );

        return equipo.getUsuarios().contains(usuario);
    }

    @Transactional
    public void anyadirDescripcionEquipo(Long equipoId, String descripcion) {
        Equipo equipo = equipoRepository.findById(equipoId)
                .orElseThrow(() -> new EquipoServiceException("Equipo no encontrado con ID: " + equipoId));

        equipo.setDescripcion(descripcion);
        equipoRepository.save(equipo);
    }


}
