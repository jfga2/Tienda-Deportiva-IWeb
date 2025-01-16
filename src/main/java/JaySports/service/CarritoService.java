package JaySports.service;

import JaySports.model.Carrito;
import JaySports.model.ProductoCarrito;
import JaySports.model.Usuario;
import JaySports.repository.CarritoRepository;
import JaySports.repository.ProductoCarritoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CarritoService {

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private ProductoCarritoRepository productoCarritoRepository;

    /**
     * Obtener el carrito asociado a un usuario.
     * Si no existe, crea un carrito nuevo para el usuario.
     *
     * @param usuario El usuario al que pertenece el carrito.
     * @return El carrito encontrado o creado.
     */
    @Transactional
    public Carrito obtenerCarritoPorUsuario(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }

        Carrito carrito = carritoRepository.findByUsuario(usuario)
                .orElseGet(() -> {
                    Carrito nuevoCarrito = new Carrito(usuario, 0.0);
                    return carritoRepository.save(nuevoCarrito);
                });

        // Forzar la carga de la colección para evitar problemas con lazy loading
        if (carrito.getProductosCarrito() != null) {
            carrito.getProductosCarrito().size();
        }

        return carrito;
    }


    @Transactional
    public void actualizarPrecioTotal(Carrito carrito) {
        if (carrito == null) {
            throw new IllegalArgumentException("El carrito no puede ser nulo");
        }

        double precioTotal = carrito.getProductosCarrito()
                .stream()
                .mapToDouble(ProductoCarrito::getSubtotal)
                .sum();

        carrito.setPrecioTotal(precioTotal);
        carritoRepository.saveAndFlush(carrito); // Usa saveAndFlush para sincronizar
    }

    /**
     * Vaciar todos los productos de un carrito.
     *
     * @param carrito El carrito que se vaciará.
     */
    @Transactional
    public void vaciarCarrito(Carrito carrito) {
        if (carrito == null) {
            throw new IllegalArgumentException("El carrito no puede ser nulo");
        }

        // Eliminar todos los productos asociados al carrito
        productoCarritoRepository.deleteByCarrito(carrito);

        // Limpiar la lista de productos y resetear el precio total
        if (carrito.getProductosCarrito() != null) {
            carrito.getProductosCarrito().clear();
        }
        carrito.setPrecioTotal(0.0);
        carritoRepository.save(carrito);
    }

    /**
     * Eliminar el carrito asociado a un usuario.
     * Verifica si existe antes de intentar eliminarlo.
     *
     * @param usuario El usuario cuyo carrito se eliminará.
     */
    @Transactional
    public void eliminarCarritoPorUsuario(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }

        Optional<Carrito> carritoOpt = carritoRepository.findByUsuario(usuario);
        if (carritoOpt.isPresent()) {
            Carrito carrito = carritoOpt.get();
            vaciarCarrito(carrito); // Vaciar el carrito antes de eliminarlo
            carritoRepository.delete(carrito);
        }
    }

    /**
     * Verificar si un carrito tiene productos.
     *
     * @param carrito El carrito que se verificará.
     * @return true si tiene productos, false de lo contrario.
     */
    @Transactional(readOnly = true)
    public boolean carritoTieneProductos(Carrito carrito) {
        if (carrito == null) {
            throw new IllegalArgumentException("El carrito no puede ser nulo");
        }

        return carrito.getProductosCarrito() != null && !carrito.getProductosCarrito().isEmpty();
    }
}
