package JaySports.repository;

import JaySports.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    // Buscar categoría por nombre
    Categoria findByNombre(String nombre);
}

