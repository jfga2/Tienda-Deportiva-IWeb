package JaySports.repository;

import JaySports.model.Pedido;
import JaySports.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByUsuarioOrderByFechaPedidoDesc(Usuario usuario);
    Optional<Pedido> findByNumeroPedido(String numeroPedido);

    @Query("SELECT p FROM Pedido p WHERE p.usuario = :usuario AND p.estado = 'PENDIENTE'")
    Optional<Pedido> findPendingByUsuario(@Param("usuario") Usuario usuario);

    @Query("SELECT p.numeroPedido FROM Pedido p WHERE p.numeroPedido LIKE CONCAT(:yearPrefix, '%') ORDER BY p.numeroPedido DESC")
    Optional<String> findLastPedidoNumberByYear(@Param("yearPrefix") String yearPrefix);
}