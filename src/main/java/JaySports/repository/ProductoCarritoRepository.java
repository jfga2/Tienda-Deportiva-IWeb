package JaySports.repository;

import JaySports.model.Carrito;
import JaySports.model.ProductoCarrito;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductoCarritoRepository extends JpaRepository<ProductoCarrito, Long> {
    List<ProductoCarrito> findByCarrito(Carrito carrito);
    void deleteByCarrito(Carrito carrito);
}
