package JaySports.controller;

import JaySports.model.Categoria;
import JaySports.service.CategoriaService;
import JaySports.authentication.ManagerUserSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private ManagerUserSession managerUserSession;

    /**
     * Listar todas las categorías.
     *
     * @param model Modelo para la vista.
     * @return La vista listarCategorias.html.
     */
    @GetMapping
    public String listarCategorias(Model model) {
        List<Categoria> categorias = categoriaService.obtenerCategorias();
        model.addAttribute("categorias", categorias);
        model.addAttribute("usuarioId", managerUserSession.usuarioLogeado());
        model.addAttribute("esAdministrador", managerUserSession.esAdministrador());
        model.addAttribute("nombreUsuario", managerUserSession.obtenerNombreUsuario());
        return "listarCategorias"; // Renderiza la vista listarCategorias.html
    }

    /**
     * Mostrar formulario para crear una nueva categoría.
     * Solo accesible por administradores.
     *
     * @param model Modelo para la vista.
     * @return La vista crearCategoria.html.
     */
    @GetMapping("/crear")
    public String mostrarFormularioCrearCategoria(Model model) {
        if (!managerUserSession.esAdministrador()) {
            return "redirect:/"; // Redirigir a la página principal si no es administrador
        }
        model.addAttribute("categoria", new Categoria());
        model.addAttribute("usuarioId", managerUserSession.usuarioLogeado());
        model.addAttribute("esAdministrador", managerUserSession.esAdministrador());
        model.addAttribute("nombreUsuario", managerUserSession.obtenerNombreUsuario());
        return "crearCategoria"; // Renderiza la vista crearCategoria.html
    }

    /**
     * Procesar el formulario para crear una nueva categoría.
     * Solo accesible por administradores.
     *
     * @param categoria Objeto categoría enviado desde el formulario.
     * @return Redirige a la lista de categorías.
     */
    @PostMapping("/crear")
    public String crearCategoria(@ModelAttribute Categoria categoria) {
        if (!managerUserSession.esAdministrador()) {
            return "redirect:/";
        }
        categoriaService.crearCategoria(categoria.getNombre());
        return "redirect:/categorias";
    }

    /**
     * Eliminar una categoría.
     * Solo accesible por administradores.
     *
     * @param id ID de la categoría a eliminar.
     * @return Redirige a la lista de categorías.
     */
    @PostMapping("/eliminar/{id}")
    public String eliminarCategoria(@PathVariable Long id) {
        if (!managerUserSession.esAdministrador()) {
            return "redirect:/";
        }
        categoriaService.eliminarCategoria(id);
        return "redirect:/categorias";
    }
}
