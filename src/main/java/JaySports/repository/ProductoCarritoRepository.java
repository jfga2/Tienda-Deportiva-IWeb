package JaySports.repository;

import JaySports.model.Carrito;
import JaySports.model.ProductoCarrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductoCarritoRepository extends JpaRepository<ProductoCarrito, Long> {

    List<ProductoCarrito> findByCarrito(Carrito carrito);

    void deleteByCarrito(Carrito carrito);

    @Modifying
    @Query("DELETE FROM ProductoCarrito pc WHERE pc.id = :id")
    void eliminarPorId(@Param("id") Long id);
}
