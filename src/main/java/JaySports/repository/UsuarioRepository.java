package JaySports.repository;

import JaySports.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// Extiende JpaRepository para habilitar paginación y ordenación
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Buscar un usuario por su email
    Optional<Usuario> findByEmail(String email);
}
