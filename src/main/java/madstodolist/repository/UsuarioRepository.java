package madstodolist.repository;

import madstodolist.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

// Extiende JpaRepository para habilitar paginación y ordenación
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Buscar un usuario por su email
    Optional<Usuario> findByEmail(String email);
}
