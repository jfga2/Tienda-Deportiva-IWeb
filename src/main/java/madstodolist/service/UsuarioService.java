package madstodolist.service;

import madstodolist.dto.UsuarioData;
import madstodolist.model.Usuario;
import madstodolist.repository.UsuarioRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    public enum LoginStatus {LOGIN_OK, USER_NOT_FOUND, ERROR_PASSWORD, USER_BLOCKED}

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ModelMapper modelMapper;

    // Método para el login del usuario
    @Transactional(readOnly = true)
    public LoginStatus login(String eMail, String password) {
        Optional<Usuario> usuario = usuarioRepository.findByEmail(eMail);

        if (!usuario.isPresent()) {
            return LoginStatus.USER_NOT_FOUND;
        } else if (usuario.get().isBloqueado()) {
            return LoginStatus.USER_BLOCKED;
        } else if (!usuario.get().getPassword().equals(password)) {
            return LoginStatus.ERROR_PASSWORD;
        } else {
            return LoginStatus.LOGIN_OK;
        }
    }

    // Método para registrar un nuevo usuario
    @Transactional
    public UsuarioData registrar(UsuarioData usuarioData) {
        Optional<Usuario> usuarioBD = usuarioRepository.findByEmail(usuarioData.getEmail());
        if (usuarioBD.isPresent()) {
            throw new UsuarioServiceException("El usuario " + usuarioData.getEmail() + " ya está registrado");
        } else if (usuarioData.getEmail() == null) {
            throw new UsuarioServiceException("El usuario no tiene email");
        } else if (usuarioData.getPassword() == null) {
            throw new UsuarioServiceException("El usuario no tiene password");
        }

        // Validamos si el nuevo usuario está intentando registrarse como administrador
        if (usuarioData.isAdministrador() && existeAdministrador()) {
            throw new UsuarioServiceException("Ya existe un administrador registrado.");
        }

        Usuario usuarioNuevo = modelMapper.map(usuarioData, Usuario.class);
        usuarioNuevo = usuarioRepository.save(usuarioNuevo);
        return modelMapper.map(usuarioNuevo, UsuarioData.class);
    }

    // Método para encontrar un usuario por su email
    @Transactional(readOnly = true)
    public UsuarioData findByEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);
        if (usuario == null) return null;
        else {
            return modelMapper.map(usuario, UsuarioData.class);
        }
    }

    // Método para encontrar un usuario por su ID
    @Transactional(readOnly = true)
    public UsuarioData findById(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);
        if (usuario == null) return null;
        else {
            return modelMapper.map(usuario, UsuarioData.class);
        }
    }

    // Método para obtener la lista de todos los usuarios
    @Transactional(readOnly = true)
    public List<UsuarioData> findAllUsuarios() {
        List<Usuario> usuarios = (List<Usuario>) usuarioRepository.findAll();
        return usuarios.stream()
                .map(usuario -> modelMapper.map(usuario, UsuarioData.class))
                .collect(Collectors.toList());
    }

    // Método para comprobar si ya existe un administrador en el sistema
    @Transactional(readOnly = true)
    public boolean existeAdministrador() {
        List<Usuario> usuarios = (List<Usuario>) usuarioRepository.findAll();
        // Devuelve verdadero si existe algún usuario que sea administrador
        return usuarios.stream().anyMatch(Usuario::isAdministrador);
    }

    // Método para saber si el usuario logueado es administrador
    @Transactional(readOnly = true)
    public boolean esAdministrador(Long idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);
        return usuario != null && usuario.isAdministrador();
    }

    // Método para bloquear o desbloquear un usuario
    @Transactional
    public void cambiarEstadoBloqueoUsuario(Long idUsuario, boolean bloquear) {
        Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);
        if (usuario == null) {
            throw new UsuarioServiceException("No existe usuario con id " + idUsuario);
        }
        usuario.setBloqueado(bloquear);
        usuarioRepository.save(usuario);
    }

    // Método para verificar si un usuario está bloqueado
    @Transactional(readOnly = true)
    public boolean isUsuarioBloqueado(Long idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);
        return usuario != null && usuario.isBloqueado();
    }
}
