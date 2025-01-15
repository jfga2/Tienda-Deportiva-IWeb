package JaySports.repository;

import JaySports.model.Comentario;
import JaySports.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {

    // Obtener todos los comentarios visibles para un producto específico
    List<Comentario> findByProductoAndVisibleTrue(Producto producto);

    // Obtener todos los comentarios de un producto, visibles o no
    List<Comentario> findByProducto(Producto producto);

    // Obtener todos los comentarios de un usuario específico
    List<Comentario> findByUsuarioId(Long usuarioId);
}

