package JaySports.service;

import JaySports.model.Categoria;
import JaySports.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    @Autowired
    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    // Crear una nueva categoría (opcionalmente con un padre)
    public Categoria crearCategoria(String nombre) {
        Categoria categoria = new Categoria();
        categoria.setNombre(nombre);
        return categoriaRepository.save(categoria);
    }

    // Obtener todas las categorías
    public List<Categoria> obtenerCategorias() {
        return categoriaRepository.findAll();
    }

    // Obtener una categoría por nombre
    public Categoria obtenerCategoriaPorNombre(String nombre) {
        return categoriaRepository.findByNombre(nombre);
    }

    // Eliminar una categoría por ID
    public void eliminarCategoria(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada con el ID: " + id));
        categoriaRepository.delete(categoria);
    }
}
