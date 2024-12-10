package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.controller.exception.UsuarioNoAutorizadoException;
import madstodolist.dto.UsuarioData;
import madstodolist.service.UsuarioService;
import madstodolist.controller.exception.UsuarioNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class UsuarioController {

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    ManagerUserSession managerUserSession;

    private void comprobarUsuarioAdministrador(Long idUsuario) {
        UsuarioData usuario = usuarioService.findById(idUsuario);
        if (usuario == null || !usuario.isAdmin()) {
            throw new UsuarioNoAutorizadoException();
        }
    }

    @GetMapping("/registrados")
    public String listadoUsuarios(Model model) {
        Long idUsuarioLogeado = managerUserSession.usuarioLogeado();
        comprobarUsuarioAdministrador(idUsuarioLogeado);

        List<UsuarioData> usuarios = usuarioService.findAllUsuarios();
        model.addAttribute("usuarios", usuarios);
        return "listaUsuarios";
    }

    @GetMapping("/registrados/{id}")
    public String descripcionUsuario(@PathVariable("id") Long id, Model model) {
        Long idUsuarioLogeado = managerUserSession.usuarioLogeado();
        comprobarUsuarioAdministrador(idUsuarioLogeado);

        UsuarioData usuario = usuarioService.findById(id);
        if (usuario == null) {
            throw new UsuarioNotFoundException();
        }
        model.addAttribute("usuario", usuario);
        return "descripcionUsuario";
    }

    @PostMapping("/registrados/{id}/bloqueo")
    public String cambiarEstadoBloqueoUsuario(@PathVariable("id") Long idUsuario,
                                              @RequestParam("bloqueado") boolean bloqueado) {
        Long idUsuarioLogeado = managerUserSession.usuarioLogeado();
        comprobarUsuarioAdministrador(idUsuarioLogeado);

        usuarioService.cambiarEstadoBloqueo(idUsuario, bloqueado);
        return "redirect:/registrados";
    }

}

