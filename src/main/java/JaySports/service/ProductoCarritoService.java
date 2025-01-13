package JaySports.service;

import JaySports.model.Carrito;
import JaySports.model.Producto;
import JaySports.model.ProductoCarrito;
import JaySports.repository.CarritoRepository;
import JaySports.repository.ProductoCarritoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoCarritoService {

    @Autowired
    private ProductoCarritoRepository productoCarritoRepository;
    private CarritoRepository carritoRepository;

    /**
     * Obtener todos los productos asociados a un carrito.
     *
     * @param carrito El carrito del que se quieren obtener los productos.
     * @return Lista de productos en el carrito.
     */
    public List<ProductoCarrito> obtenerProductosPorCarrito(Carrito carrito) {
        return productoCarritoRepository.findByCarrito(carrito);
    }

    /**
     * Agregar un producto al carrito.
     * Si el producto ya existe en el carrito, este método no lo verifica.
     * La lógica para evitar duplicados debe manejarse desde el controlador o nivel superior.
     *
     * @param carrito  El carrito al que se va a agregar el producto.
     * @param producto El producto que se va a agregar.
     * @param cantidad La cantidad del producto que se desea agregar.
     * @return El productoCarrito agregado.
     */
    public ProductoCarrito agregarProductoAlCarrito(Carrito carrito, Producto producto, int cantidad) {
        ProductoCarrito productoCarrito = new ProductoCarrito(
                carrito,
                producto,
                cantidad,
                producto.getPrecio(),
                producto.getPrecio() * cantidad
        );

        // Agregar a la lista del carrito
        carrito.getProductosCarrito().add(productoCarrito);

        // Guardar el carrito
        carritoRepository.save(carrito);

        return productoCarritoRepository.save(productoCarrito);
    }

    /**
     * Guardar un producto del carrito.
     * Este método se utiliza tanto para agregar un nuevo producto como para actualizar uno existente.
     *
     * @param productoCarrito El productoCarrito que se desea guardar o actualizar.
     * @return El productoCarrito guardado o actualizado.
     */
    public ProductoCarrito guardarProductoCarrito(ProductoCarrito productoCarrito) {
        return productoCarritoRepository.save(productoCarrito);
    }

    /**
     * Eliminar un producto del carrito.
     *
     * @param productoCarrito El productoCarrito que se desea eliminar.
     */
    public void eliminarProductoDelCarrito(ProductoCarrito productoCarrito) {
        productoCarritoRepository.delete(productoCarrito);
    }

    /**
     * Actualizar la cantidad de un producto en el carrito.
     * Si la cantidad es 0 o negativa, elimina el producto del carrito.
     *
     * @param productoCarrito El productoCarrito que se desea actualizar.
     * @param nuevaCantidad   La nueva cantidad que se quiere establecer.
     * @return El productoCarrito actualizado o null si fue eliminado.
     */
    public ProductoCarrito actualizarCantidad(ProductoCarrito productoCarrito, int nuevaCantidad) {
        if (nuevaCantidad <= 0) {
            productoCarritoRepository.delete(productoCarrito);
            return null;
        }
        productoCarrito.setCantidad(nuevaCantidad);
        productoCarrito.setSubtotal(productoCarrito.getPrecioUnitario() * nuevaCantidad);
        return productoCarritoRepository.save(productoCarrito);
    }

    /**
     * Vaciar un carrito eliminando todos los productos asociados.
     *
     * @param carrito El carrito que se desea vaciar.
     */
    public void vaciarCarrito(Carrito carrito) {
        List<ProductoCarrito> productos = productoCarritoRepository.findByCarrito(carrito);
        productoCarritoRepository.deleteAll(productos);
    }
}
