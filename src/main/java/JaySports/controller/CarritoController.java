package JaySports.controller;

import JaySports.model.Carrito;
import JaySports.model.Producto;
import JaySports.model.ProductoCarrito;
import JaySports.model.Usuario;
import JaySports.service.CarritoService;
import JaySports.authentication.ManagerUserSession;
import JaySports.service.ProductoCarritoService;
import JaySports.service.ProductoService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/carrito")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    @Autowired
    private ProductoCarritoService productoCarritoService;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private ManagerUserSession managerUserSession;

    /**
     * Mostrar la vista del carrito de compras.
     *
     * @param model Modelo para pasar datos a la vista.
     * @return La vista carrito.html.
     */
    @GetMapping
    public String mostrarCarrito(Model model) {
        // Verificar si el usuario está logueado
        Usuario usuario = managerUserSession.obtenerUsuarioLogeado();
        if (usuario == null) {
            return "redirect:/login"; // Redirigir al login si no está logueado
        }

        // Obtener el carrito del usuario
        Carrito carrito = carritoService.obtenerCarritoPorUsuario(usuario);

        // Obtener los productos en el carrito
        List<ProductoCarrito> productosCarrito = productoCarritoService.obtenerProductosPorCarrito(carrito);

        // Calcular el precio total del carrito
        double precioTotal = productosCarrito.stream()
                .mapToDouble(ProductoCarrito::getSubtotal)
                .sum();

        // Pasar datos al modelo
        model.addAttribute("productosCarrito", productosCarrito);
        model.addAttribute("precioTotal", precioTotal);
        model.addAttribute("cantidadProductosEnCarrito", productosCarrito.size());
        model.addAttribute("usuarioId", managerUserSession.usuarioLogeado());
        model.addAttribute("esAdministrador", managerUserSession.esAdministrador());
        model.addAttribute("nombreUsuario", managerUserSession.obtenerNombreUsuario());

        return "carrito";
    }

    /**
     * Añadir un producto al carrito.
     *
     * @param productoId ID del producto que se desea añadir.
     * @param model      Modelo para pasar datos a la vista.
     * @return Redirige a la página anterior o al carrito.
     */
    @PostMapping("/agregar/{productoId}")
    @Transactional
    public String agregarProductoAlCarrito(@PathVariable Long productoId, Model model) {
        // Verificar si el usuario está logueado
        Usuario usuario = managerUserSession.obtenerUsuarioLogeado();
        if (usuario == null) {
            return "redirect:/login"; // Redirigir al login si no está logueado
        }

        // Obtener el carrito del usuario
        Carrito carrito = carritoService.obtenerCarritoPorUsuario(usuario);

        // Inicializar la colección productosCarrito para evitar LazyInitializationException
        carrito.getProductosCarrito().size();

        // Obtener el producto desde la base de datos
        Producto producto = productoService.obtenerProductoPorId(productoId);

        // Verificar si el producto ya está en el carrito
        ProductoCarrito productoExistente = carrito.getProductosCarrito().stream()
                .filter(pc -> pc.getProducto().getId().equals(productoId))
                .findFirst()
                .orElse(null);

        if (productoExistente != null) {
            // Si el producto ya existe, incrementar su cantidad
            productoCarritoService.actualizarCantidad(productoExistente, productoExistente.getCantidad() + 1);
        } else {
            // Si el producto no existe, añadirlo al carrito
            productoCarritoService.agregarProductoAlCarrito(carrito, producto, 1);
        }

        // Actualizar el precio total del carrito
        carritoService.actualizarPrecioTotal(carrito);

        return "redirect:/productos"; // Redirigir a la lista de productos
    }

    /**
     * Eliminar un producto del carrito.
     *
     * @param productoCarritoId ID del ProductoCarrito que se desea eliminar.
     * @return Redirige a la vista del carrito.
     */
    @PostMapping("/eliminar/{productoCarritoId}")
    public String eliminarProductoDelCarrito(@PathVariable Long productoCarritoId) {
        // Obtener el producto del carrito
        ProductoCarrito productoCarrito = productoCarritoService.obtenerProductosPorCarrito(null).stream()
                .filter(pc -> pc.getId().equals(productoCarritoId))
                .findFirst()
                .orElse(null);

        if (productoCarrito != null) {
            productoCarritoService.eliminarProductoDelCarrito(productoCarrito);

            // Actualizar el precio total del carrito
            Carrito carrito = productoCarrito.getCarrito();
            carritoService.actualizarPrecioTotal(carrito);
        }

        return "redirect:/carrito";
    }
}
