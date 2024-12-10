package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.dto.UsuarioData;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ManagerUserSession managerUserSession;

    // Método para añadir atributos comunes (nombreUsuario y usuarioId) a todas las vistas
    @ModelAttribute
    public void addAttributes(Model model) {
        Long idUsuarioLogeado = managerUserSession.usuarioLogeado();
        if (idUsuarioLogeado != null) {
            UsuarioData usuario = usuarioService.findById(idUsuarioLogeado);
            if (usuario != null) {
                model.addAttribute("nombreUsuario", usuario.getNombre());
                model.addAttribute("usuarioId", usuario.getId());
            }
        } else {
            model.addAttribute("nombreUsuario", null);
            model.addAttribute("usuarioId", null);
        }
    }

    // Método que maneja la ruta /registrados y lista los usuarios
    @GetMapping("/registrados")
    public String listaUsuariosRegistrados(Model model) {
        Long idUsuarioLogeado = managerUserSession.usuarioLogeado();
        if (idUsuarioLogeado == null) {
            return "redirect:/login";
        }

        // Comprobamos si el usuario es administrador
        if (!managerUserSession.esAdministrador()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No tiene permisos para acceder a esta página.");
        }

        // Mostramos la lista de usuarios
        List<UsuarioData> usuarios = usuarioService.findAllUsuarios();
        model.addAttribute("usuarios", usuarios);
        return "listaUsuariosRegistrados";
    }

    // Método que maneja la descripción del usuario
    @GetMapping("/registrados/{id}")
    public String descripcionUsuario(@PathVariable Long id, Model model) {
        Long idUsuarioLogeado = managerUserSession.usuarioLogeado();
        if (idUsuarioLogeado == null) {
            return "redirect:/login";
        }

        // Comprobamos si el usuario es administrador
        if (!managerUserSession.esAdministrador()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No tiene permisos para acceder a esta página.");
        }

        // Mostramos la descripción del usuario
        UsuarioData usuario = usuarioService.findById(id);
        if (usuario == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
        }
        model.addAttribute("usuario", usuario);
        return "usuarioDescripcion";
    }

    // Método para bloquear o desbloquear un usuario
    @PostMapping("/registrados/{id}/bloquear")
    public String bloquearUsuario(@PathVariable Long id, @RequestParam("accion") String accion) {
        Long idUsuarioLogeado = managerUserSession.usuarioLogeado();
        if (idUsuarioLogeado == null) {
            return "redirect:/login";
        }

        // Comprobamos si el usuario es administrador
        if (!managerUserSession.esAdministrador()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No tiene permisos para realizar esta acción.");
        }

        // Bloqueo o desbloqueo del usuario según la acción solicitada
        boolean bloquear = "bloquear".equals(accion);
        usuarioService.cambiarEstadoBloqueoUsuario(id, bloquear);

        return "redirect:/registrados";
    }
}
