package JaySports.controller;

import JaySports.model.Carrito;
import JaySports.model.Producto;
import JaySports.model.ProductoCarrito;
import JaySports.model.Usuario;
import JaySports.service.CarritoService;
import JaySports.authentication.ManagerUserSession;
import JaySports.service.ProductoCarritoService;
import JaySports.service.ProductoService;
import JaySports.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private PedidoService pedidoService;

    @Autowired
    private ManagerUserSession managerUserSession;

    private static final Logger logger = LoggerFactory.getLogger(CarritoController.class);

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
     * @return Redirige a la página anterior o al carrito.
     */
    @PostMapping("/agregar/{productoId}")
    public String agregarProductoAlCarrito(@PathVariable Long productoId) {
        logger.info("Inicio de agregarProductoAlCarrito con productoId: {}", productoId);

        Usuario usuario = managerUserSession.obtenerUsuarioLogeado();
        if (usuario == null) {
            logger.warn("Usuario no logueado, redirigiendo a login.");
            return "redirect:/login";
        }

        logger.info("Usuario logueado: {}", usuario.getId());

        Carrito carrito = carritoService.obtenerCarritoPorUsuario(usuario);
        logger.info("Carrito obtenido: {}", carrito.getId());

        Producto producto = productoService.obtenerProductoPorId(productoId);
        logger.info("Producto obtenido: {} - {}", producto.getId(), producto.getNombre());

        ProductoCarrito productoExistente = carrito.getProductosCarrito().stream()
                .filter(pc -> pc.getProducto().getId().equals(productoId))
                .findFirst()
                .orElse(null);

        if (productoExistente != null) {
            logger.info("Producto ya existe en el carrito. Incrementando cantidad.");
            productoCarritoService.actualizarCantidad(productoExistente, productoExistente.getCantidad() + 1);
        } else {
            logger.info("Producto no encontrado en el carrito. Agregándolo.");
            productoCarritoService.agregarProductoAlCarrito(carrito, producto, 1);
        }

        carritoService.actualizarPrecioTotal(carrito);
        logger.info("Precio total del carrito actualizado: {}", carrito.getPrecioTotal());

        return "redirect:/productos";
    }

    /**
     * Eliminar un producto del carrito.
     */
    @PostMapping("/eliminar/{productoCarritoId}")
    public String eliminarProductoDelCarrito(@PathVariable Long productoCarritoId) {
        Usuario usuario = managerUserSession.obtenerUsuarioLogeado();
        if (usuario == null) {
            return "redirect:/login";
        }

        // Obtener el carrito del usuario
        Carrito carrito = carritoService.obtenerCarritoPorUsuario(usuario);

        // Verificar si el producto pertenece al carrito del usuario
        productoCarritoService.eliminarProductoPorId(productoCarritoId);

        // Actualizar el precio total del carrito
        carritoService.actualizarPrecioTotal(carrito);

        return "redirect:/carrito";
    }
    /**
     * Incrementar la cantidad de un producto en el carrito.
     *
     * @param productoCarritoId ID del ProductoCarrito cuya cantidad se desea incrementar.
     * @return Redirige a la vista del carrito.
     */
    @PostMapping("/incrementar/{productoCarritoId}")
    public String incrementarCantidadProducto(@PathVariable Long productoCarritoId, Model model) {
        logger.debug("Iniciando incremento de cantidad para producto ID: {}", productoCarritoId);

        Usuario usuario = managerUserSession.obtenerUsuarioLogeado();
        if (usuario == null) {
            logger.warn("Usuario no logueado, redirigiendo a login.");
            return "redirect:/login";
        }

        Carrito carrito = carritoService.obtenerCarritoPorUsuario(usuario);
        logger.debug("Carrito obtenido: {}", carrito.getId());

        ProductoCarrito productoCarrito = productoCarritoService.obtenerProductosPorCarrito(carrito).stream()
                .filter(pc -> pc.getId().equals(productoCarritoId))
                .findFirst()
                .orElse(null);

        if (productoCarrito != null) {
            logger.debug("Producto encontrado en el carrito: {}", productoCarrito.getProducto().getNombre());
            productoCarritoService.actualizarCantidad(productoCarrito, productoCarrito.getCantidad() + 1);
            carritoService.actualizarPrecioTotal(carrito);
            logger.info("Cantidad actualizada correctamente. Nueva cantidad: {}", productoCarrito.getCantidad());
        } else {
            logger.error("Producto no encontrado en el carrito.");
        }

        return "redirect:/carrito";
    }

    /**
     * Decrementar la cantidad de un producto en el carrito.
     *
     * @param productoCarritoId ID del ProductoCarrito cuya cantidad se desea decrementar.
     * @return Redirige a la vista del carrito.
     */
    @PostMapping("/decrementar/{productoCarritoId}")
    public String decrementarCantidadProducto(@PathVariable Long productoCarritoId) {
        Usuario usuario = managerUserSession.obtenerUsuarioLogeado();
        if (usuario == null) {
            return "redirect:/login";
        }

        Carrito carrito = carritoService.obtenerCarritoPorUsuario(usuario);
        ProductoCarrito productoCarrito = productoCarritoService.obtenerProductosPorCarrito(carrito).stream()
                .filter(pc -> pc.getId().equals(productoCarritoId))
                .findFirst()
                .orElse(null);

        if (productoCarrito != null) {
            if (productoCarrito.getCantidad() > 1) {
                productoCarritoService.actualizarCantidad(productoCarrito, productoCarrito.getCantidad() - 1);
            } else {
                productoCarritoService.eliminarProductoDelCarrito(productoCarrito);
            }
            carritoService.actualizarPrecioTotal(carrito);
        }

        return "redirect:/carrito";
    }

    /**
     * Finalizar la compra.
     *
     * @param model Modelo para pasar datos a la vista.
     * @return Redirige a la página de pedidos o carrito en caso de error.
     */
    @PostMapping("/finalizar")
    public String finalizarCompra(Model model) {
        logger.debug("Iniciando finalizarCompra");

        Usuario usuario = managerUserSession.obtenerUsuarioLogeado();
        if (usuario == null) {
            return "redirect:/login";
        }

        try {
            Carrito carrito = carritoService.obtenerCarritoPorUsuario(usuario);
            if (carrito.getProductosCarrito().isEmpty()) {
                model.addAttribute("error", "El carrito está vacío");
                return "redirect:/carrito";
            }

            pedidoService.crearPedidoDesdeCarrito(usuario, carrito);
            return "redirect:/productos";
        } catch (IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/carrito";
        }
    }
}
