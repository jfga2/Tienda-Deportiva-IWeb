package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.dto.UsuarioData;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

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

    // Método para añadir atributos comunes (usuario logueado, administrador)
    @ModelAttribute
    public void addAttributes(Model model) {
        Long idUsuarioLogeado = managerUserSession.usuarioLogeado();
        boolean esAdministrador = managerUserSession.esAdministrador();

        if (idUsuarioLogeado != null) {
            UsuarioData usuario = usuarioService.findById(idUsuarioLogeado);
            if (usuario != null) {
                model.addAttribute("nombreUsuario", usuario.getNombre());
                model.addAttribute("usuarioId", usuario.getId());
                model.addAttribute("esAdministrador", esAdministrador);
            }
        } else {
            model.addAttribute("nombreUsuario", null);
            model.addAttribute("usuarioId", null);
            model.addAttribute("esAdministrador", false);
        }
    }

    @GetMapping("/welcome")
    public String welcome(Model model) {
        // Imágenes del carrusel
        List<String> carouselImages = List.of(
                "/images/image1.jpg",
                "/images/image2.jpg",
                "/images/image3.jpg"
        );

        // Imágenes de productos
        List<String> productImages = List.of(
                "/images/product1.jpg",
                "/images/product2.jpg",
                "/images/product3.jpg"
        );

        // Imágenes de "Descubre más"
        List<String> discoverImages = List.of(
                "/images/product4.jpg",
                "/images/product5.jpg",
                "/images/product6.jpg"
        );

        model.addAttribute("carouselImages", carouselImages);
        model.addAttribute("productImages", productImages);
        model.addAttribute("discoverImages", discoverImages);

        return "welcome";
    }
}
