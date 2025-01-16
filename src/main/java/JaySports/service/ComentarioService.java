package JaySports.service;

import JaySports.model.Comentario;
import JaySports.model.Producto;
import JaySports.model.Usuario;
import JaySports.repository.ComentarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ComentarioService {

    private final ComentarioRepository comentarioRepository;

    @Autowired
    public ComentarioService(ComentarioRepository comentarioRepository) {
        this.comentarioRepository = comentarioRepository;
    }

    // Crear un nuevo comentario
    public Comentario crearComentario(Producto producto, Usuario usuario, String contenido) {
        Comentario comentario = new Comentario();
        comentario.setProducto(producto);
        comentario.setUsuario(usuario);
        comentario.setContenido(contenido);
        comentario.setFechaCreacion(new Date());
        comentario.setVisible(true); // Los comentarios por defecto son visibles
        return comentarioRepository.save(comentario);
    }

    // Obtener comentarios visibles para un producto
    public List<Comentario> obtenerComentariosVisibles(Producto producto) {
        return comentarioRepository.findByProductoAndVisibleTrue(producto);
    }

    // Moderar un comentario (hacerlo visible o no)
    public Comentario moderarComentario(Long comentarioId, boolean visible) {
        Comentario comentario = comentarioRepository.findById(comentarioId)
                .orElseThrow(() -> new IllegalArgumentException("Comentario no encontrado"));
        comentario.setVisible(visible);
        return comentarioRepository.save(comentario);
    }

    // Obtener comentarios de un usuario específico
    public List<Comentario> obtenerComentariosPorUsuario(Long usuarioId) {
        return comentarioRepository.findByUsuarioId(usuarioId);
    }
}
