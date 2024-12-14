package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.dto.UsuarioData;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class HomeController {

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    ManagerUserSession managerUserSession;

    @GetMapping("/about")
    public String about(Model model) {
        return "about";
    }

    // Método para añadir atributos comunes a todas las vistas, como el usuario logueado y si es administrador
    @ModelAttribute
    public void addAttributes(Model model) {
        Long idUsuarioLogeado = managerUserSession.usuarioLogeado();
        boolean esAdministrador = managerUserSession.esAdministrador();

        if (idUsuarioLogeado != null) {
            UsuarioData usuario = usuarioService.findById(idUsuarioLogeado);
            if (usuario != null) {
                model.addAttribute("nombreUsuario", usuario.getNombre());
                model.addAttribute("usuarioId", usuario.getId());
                model.addAttribute("esAdministrador", esAdministrador); // Añadir el atributo "esAdministrador"
            } else {
                model.addAttribute("nombreUsuario", null);
                model.addAttribute("usuarioId", null);
                model.addAttribute("esAdministrador", false); // Si no hay usuario logueado, no es admin
            }
        } else {
            model.addAttribute("nombreUsuario", null);
            model.addAttribute("usuarioId", null);
            model.addAttribute("esAdministrador", false); // Si no hay usuario logueado, no es admin
        }
    }

    @GetMapping("/welcome")
    public String welcome(Model model) {
        return "welcome";
    }

}
