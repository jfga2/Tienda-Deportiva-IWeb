package JaySports.service;

import JaySports.controller.CarritoController;
import JaySports.model.Carrito;
import JaySports.model.Producto;
import JaySports.model.ProductoCarrito;
import JaySports.repository.CarritoRepository;
import JaySports.repository.ProductoCarritoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@Service
public class ProductoCarritoService {

    @Autowired
    private ProductoCarritoRepository productoCarritoRepository;

    @Autowired
    private CarritoRepository carritoRepository;

    private static final Logger logger = LoggerFactory.getLogger(CarritoController.class);

    /**
     * Obtener todos los productos asociados a un carrito.
     * Carga explícitamente los datos relacionados con los productos para evitar LazyInitializationException.
     *
     * @param carrito El carrito del que se quieren obtener los productos.
     * @return Lista de productos en el carrito.
     */
    @Transactional(readOnly = true)
    public List<ProductoCarrito> obtenerProductosPorCarrito(Carrito carrito) {
        List<ProductoCarrito> productosCarrito = productoCarritoRepository.findByCarrito(carrito);

        // Forzar la carga explícita de los datos relacionados con Producto
        productosCarrito.forEach(productoCarrito -> productoCarrito.getProducto().getFoto());

        return productosCarrito;
    }

    /**
     * Agregar un producto al carrito o actualizar su cantidad si ya existe.
     *
     * @param carrito  El carrito al que se va a agregar el producto.
     * @param producto El producto que se va a agregar o actualizar.
     * @param cantidad La cantidad del producto que se desea agregar.
     * @return El productoCarrito agregado o actualizado.
     */
    @Transactional
    public ProductoCarrito agregarProductoAlCarrito(Carrito carrito, Producto producto, int cantidad) {
        logger.info("Inicio de agregarProductoAlCarrito en ProductoCarritoService. Carrito ID: {}, Producto ID: {}, Cantidad: {}", carrito.getId(), producto.getId(), cantidad);

        if (carrito == null) {
            logger.error("El carrito es nulo.");
            throw new IllegalArgumentException("El carrito no puede ser nulo");
        }
        if (producto == null) {
            logger.error("El producto es nulo.");
            throw new IllegalArgumentException("El producto no puede ser nulo");
        }

        Carrito carritoManaged = carritoRepository.findById(carrito.getId())
                .orElseThrow(() -> new IllegalArgumentException("Carrito no encontrado"));

        ProductoCarrito productoExistente = carritoManaged.getProductosCarrito().stream()
                .filter(pc -> pc.getProducto().getId().equals(producto.getId()))
                .findFirst()
                .orElse(null);

        if (productoExistente != null) {
            logger.info("Producto ya existe en el carrito. Incrementando cantidad.");
            int nuevaCantidad = productoExistente.getCantidad() + cantidad;
            productoExistente.setCantidad(nuevaCantidad);
            productoExistente.setSubtotal(productoExistente.getPrecioUnitario() * nuevaCantidad);
            logger.info("Cantidad actualizada: {}, Subtotal actualizado: {}", nuevaCantidad, productoExistente.getSubtotal());
            return productoCarritoRepository.save(productoExistente);
        } else {
            logger.info("Producto no encontrado en el carrito. Creando nuevo ProductoCarrito.");
            ProductoCarrito nuevoProductoCarrito = new ProductoCarrito(
                    carritoManaged,
                    producto,
                    cantidad,
                    producto.getPrecio(),
                    producto.getPrecio() * cantidad
            );


            logger.info("Nuevo ProductoCarrito creado y agregado al carrito.");
            return productoCarritoRepository.save(nuevoProductoCarrito);
        }
    }

    /**
     * Guardar un producto del carrito.
     * Este método se utiliza tanto para agregar un nuevo producto como para actualizar uno existente.
     *
     * @param productoCarrito El productoCarrito que se desea guardar o actualizar.
     * @return El productoCarrito guardado o actualizado.
     */
    @Transactional
    public ProductoCarrito guardarProductoCarrito(ProductoCarrito productoCarrito) {
        return productoCarritoRepository.save(productoCarrito);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void eliminarProductoDelCarrito(ProductoCarrito productoCarrito) {
        if (productoCarrito == null) {
            throw new IllegalArgumentException("El producto del carrito no puede ser nulo");
        }

        // Refrescar la entidad para sincronizar con la base de datos
        productoCarritoRepository.findById(productoCarrito.getId()).ifPresent(productoCarritoRefrescado -> {
            productoCarritoRepository.delete(productoCarritoRefrescado);
        });
    }

    /**
     * Método para eliminar un producto del carrito usando eliminación directa.
     */
    @Transactional
    public void eliminarProductoPorId(Long productoCarritoId) {
        productoCarritoRepository.eliminarPorId(productoCarritoId);
    }


    /**
     * Actualizar la cantidad de un producto en el carrito.
     * Si la cantidad es 0 o negativa, elimina el producto del carrito.
     *
     * @param productoCarrito El productoCarrito que se desea actualizar.
     * @param nuevaCantidad   La nueva cantidad que se quiere establecer.
     * @return El productoCarrito actualizado o null si fue eliminado.
     */
    @Transactional
    public ProductoCarrito actualizarCantidad(ProductoCarrito productoCarrito, int nuevaCantidad) {
        logger.debug("Actualizando cantidad para ProductoCarrito ID: {}", productoCarrito.getId());
        logger.debug("Nueva cantidad: {}", nuevaCantidad);

        if (productoCarrito == null) {
            logger.error("El producto del carrito es nulo.");
            throw new IllegalArgumentException("El producto del carrito no puede ser nulo");
        }

        if (nuevaCantidad <= 0) {
            logger.warn("Cantidad menor o igual a 0, eliminando el producto del carrito.");
            productoCarritoRepository.delete(productoCarrito);
            return null;
        }

        productoCarrito.setCantidad(nuevaCantidad);
        productoCarrito.setSubtotal(productoCarrito.getPrecioUnitario() * nuevaCantidad);
        logger.info("Producto actualizado. Nueva cantidad: {}, Nuevo subtotal: {}", nuevaCantidad, productoCarrito.getSubtotal());

        return productoCarritoRepository.save(productoCarrito);
    }

    /**
     * Vaciar un carrito eliminando todos los productos asociados.
     *
     * @param carrito El carrito que se desea vaciar.
     */
    @Transactional
    public void vaciarCarrito(Carrito carrito) {
        if (carrito == null) {
            throw new IllegalArgumentException("El carrito no puede ser nulo");
        }

        List<ProductoCarrito> productos = productoCarritoRepository.findByCarrito(carrito);
        productoCarritoRepository.deleteAll(productos);

        // Limpiar la lista de productos del carrito para reflejar el cambio en memoria
        if (carrito.getProductosCarrito() != null) {
            carrito.getProductosCarrito().clear();
        }

        carrito.setPrecioTotal(0.0);
        carritoRepository.save(carrito);
    }
}
