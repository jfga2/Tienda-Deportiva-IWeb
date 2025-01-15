package JaySports.service;

import JaySports.model.Carrito;
import JaySports.model.ProductoCarrito;
import JaySports.model.Usuario;
import JaySports.repository.CarritoRepository;
import JaySports.repository.ProductoCarritoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CarritoService {

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private ProductoCarritoRepository productoCarritorepository;

    @Transactional
    public Carrito obtenerCarritoPorUsuario(Usuario usuario) {
        Carrito carrito = carritoRepository.findByUsuario(usuario)
                .orElseGet(() -> {
                    Carrito nuevoCarrito = new Carrito(usuario, 0.0);
                    return carritoRepository.save(nuevoCarrito);
                });

        // Forzar la carga de la colección
        if (carrito != null) {
            carrito.getProductosCarrito().size();
        }

        return carrito;
    }

    public void actualizarPrecioTotal(Carrito carrito) {
        double precioTotal = carrito.getProductosCarrito()
                .stream()
                .mapToDouble(ProductoCarrito::getSubtotal) // Uso de ProductoCarrito
                .sum();
        carrito.setPrecioTotal(precioTotal);
        carritoRepository.save(carrito);
    }

    @Transactional
    public void vaciarCarrito(Carrito carrito) {
        // Eliminar todos los productos del carrito
        productoCarritorepository.deleteByCarrito(carrito);

        // Resetear la lista de productos y el precio total
        carrito.getProductosCarrito().clear();
        carrito.setPrecioTotal(0.0);
        carritoRepository.save(carrito);
    }
}
