package JaySports.service;

import JaySports.model.Carrito;
import JaySports.model.ProductoCarrito;
import JaySports.model.Usuario;
import JaySports.repository.CarritoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CarritoService {

    @Autowired
    private CarritoRepository carritoRepository;

    public Carrito obtenerCarritoPorUsuario(Usuario usuario) {
        return carritoRepository.findByUsuario(usuario)
                .orElseGet(() -> {
                    Carrito nuevoCarrito = new Carrito(usuario, 0.0);
                    return carritoRepository.save(nuevoCarrito);
                });
    }

    public void actualizarPrecioTotal(Carrito carrito) {
        double precioTotal = carrito.getProductosCarrito()
                .stream()
                .mapToDouble(ProductoCarrito::getSubtotal) // Uso de ProductoCarrito
                .sum();
        carrito.setPrecioTotal(precioTotal);
        carritoRepository.save(carrito);
    }
}
