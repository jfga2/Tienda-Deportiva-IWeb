package JaySports.controller;

import JaySports.dto.ProductoData;
import JaySports.model.Producto;
import JaySports.service.ProductoService;
import JaySports.authentication.ManagerUserSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@Controller
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private ManagerUserSession managerUserSession;

    /**
     * Mostrar la vista de creación de productos.
     * Solo accesible por administradores.
     *
     * @param model Modelo para la vista.
     * @return La vista crearProducto.html.
     */
    @GetMapping("/crearProducto")
    public String mostrarFormularioCrearProducto(Model model) {
        // Verificar si el usuario es administrador
        if (!managerUserSession.esAdministrador()) {
            return "redirect:/"; // Redirigir a la página principal si no es administrador
        }

        // Añadir atributos al modelo
        model.addAttribute("productoData", new ProductoData());
        model.addAttribute("usuarioId", managerUserSession.usuarioLogeado());
        model.addAttribute("esAdministrador", managerUserSession.esAdministrador());
        model.addAttribute("nombreUsuario", managerUserSession.obtenerNombreUsuario());

        return "crearProducto"; // Renderiza la vista crearProducto.html
    }

    /**
     * Procesar el formulario de creación de productos.
     * Solo accesible por administradores.
     *
     * @param productoData Datos del producto enviados desde el formulario.
     * @param bindingResult Resultado de la validación.
     * @param model Modelo para la vista.
     * @return La vista crearProducto.html si hay errores, o redirige al listado de productos si tiene éxito.
     */
    @PostMapping("/crearProducto")
    public String crearProducto(@Valid @ModelAttribute("productoData") ProductoData productoData,
                                BindingResult bindingResult, Model model) {
        // Comprobar si el usuario es administrador
        if (!managerUserSession.esAdministrador()) {
            return "redirect:/";
        }

        // Validar el formulario
        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", "Por favor, corrige los errores del formulario.");
            model.addAttribute("productoData", productoData);
            model.addAttribute("usuarioId", managerUserSession.usuarioLogeado());
            model.addAttribute("esAdministrador", managerUserSession.esAdministrador());
            model.addAttribute("nombreUsuario", managerUserSession.obtenerNombreUsuario());
            return "crearProducto";
        }

        try {
            // Crear el producto usando el servicio
            productoService.crearProducto(productoData);
            model.addAttribute("successMessage", "El producto se ha creado exitosamente.");
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "crearProducto";
        }

        // Redirigir después de 3 segundos
        return "redirect:/productos";
    }

    /**
     * Listar productos con paginación.
     *
     * @param page  Página actual (empieza desde 0).
     * @param size  Cantidad de productos por página.
     * @param model Modelo para la vista.
     * @return La vista listarProductos.html.
     */
    @GetMapping("/productos")
    public String listarProductos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            Model model
    ) {
        // Obtener la lista de productos paginada
        List<Producto> productos = productoService.listarProductos(page, size);

        // Verificar si hay más productos para la siguiente página
        boolean hasNextPage = productoService.hayMasProductos(page, size);

        // Pasar datos a la vista
        model.addAttribute("productos", productos);
        model.addAttribute("currentPage", page);
        model.addAttribute("hasNextPage", hasNextPage);
        model.addAttribute("usuarioId", managerUserSession.usuarioLogeado());
        model.addAttribute("esAdministrador", managerUserSession.esAdministrador());
        model.addAttribute("nombreUsuario", managerUserSession.obtenerNombreUsuario());

        return "listarProductos"; // Renderiza la vista listarProductos.html
    }

    /**
     * Mostrar el formulario para editar un producto.
     *
     * @param id    ID del producto a editar.
     * @param model Modelo para la vista.
     * @return La vista editarProducto.html.
     */
    @GetMapping("/editarProducto/{id}")
    public String mostrarFormularioEditarProducto(@PathVariable Long id, Model model) {
        // Verificar si el usuario es administrador
        if (!managerUserSession.esAdministrador()) {
            return "redirect:/"; // Redirigir a la página principal si no es administrador
        }

        // Obtener el producto desde la base de datos
        Producto producto = productoService.obtenerProductoPorId(id);

        // Mapear a ProductoData y pasarlo al modelo
        ProductoData productoData = new ProductoData(
                producto.getId(),
                producto.getNombre(),
                producto.getCategoria(),
                producto.getPrecio(),
                producto.getDescripcion(),
                producto.getFoto(),
                producto.getStock()
        );
        model.addAttribute("productoData", productoData);
        model.addAttribute("usuarioId", managerUserSession.usuarioLogeado());
        model.addAttribute("esAdministrador", managerUserSession.esAdministrador());
        model.addAttribute("nombreUsuario", managerUserSession.obtenerNombreUsuario());

        return "editarProducto"; // Renderiza la vista editarProducto.html
    }

    /**
     * Procesar la edición de un producto.
     *
     * @param id            ID del producto a editar.
     * @param productoData  Datos editados del producto.
     * @param bindingResult Resultado de la validación.
     * @param model         Modelo para la vista.
     * @return Redirige a la lista de productos o vuelve a la vista de edición si hay errores.
     */
    @PostMapping("/editarProducto/{id}")
    public String editarProducto(
            @PathVariable Long id,
            @Valid @ModelAttribute("productoData") ProductoData productoData,
            BindingResult bindingResult,
            Model model
    ) {
        // Verificar si el usuario es administrador
        if (!managerUserSession.esAdministrador()) {
            return "redirect:/";
        }

        // Validar los datos del formulario
        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", "Por favor, corrige los errores del formulario.");
            model.addAttribute("productoData", productoData);
            model.addAttribute("usuarioId", managerUserSession.usuarioLogeado());
            model.addAttribute("esAdministrador", managerUserSession.esAdministrador());
            model.addAttribute("nombreUsuario", managerUserSession.obtenerNombreUsuario());
            return "editarProducto";
        }

        // Actualizar el producto en la base de datos
        productoService.actualizarProducto(id, productoData);

        // Redirigir a la lista de productos
        return "redirect:/productos";
    }

    /**
     * Eliminar un producto.
     * Solo accesible por administradores.
     *
     * @param id ID del producto a eliminar.
     * @return Redirige a la lista de productos.
     */
    @PostMapping("/eliminarProducto/{id}")
    public String eliminarProducto(@PathVariable Long id) {
        // Verificar si el usuario es administrador
        if (!managerUserSession.esAdministrador()) {
            return "redirect:/"; // Redirigir si no es administrador
        }

        try {
            productoService.eliminarProducto(id);
        } catch (IllegalArgumentException e) {
            return "redirect:/productos?error=" + e.getMessage();
        }

        return "redirect:/productos";
    }
}
