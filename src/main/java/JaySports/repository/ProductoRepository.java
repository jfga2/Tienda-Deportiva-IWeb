package JaySports.repository;

import JaySports.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Buscar un producto por su nombre exacto
    Optional<Producto> findByNombre(String nombre);

    // Buscar productos por una categoría específica
    List<Producto> findByCategoria(String categoria);

    // Buscar productos cuyo nombre contenga una palabra o frase específica
    List<Producto> findByNombreContaining(String fragmento);

    // Buscar productos con un stock mayor que el especificado
    List<Producto> findByStockGreaterThan(Integer stock);

    // Buscar productos ordenados por precio ascendente
    List<Producto> findAllByOrderByPrecioAsc();
}
