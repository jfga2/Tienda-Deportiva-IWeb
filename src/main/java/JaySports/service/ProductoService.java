package JaySports.service;

import JaySports.dto.ProductoData;
import JaySports.model.Producto;
import JaySports.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    /**
     * Crear un nuevo producto en la base de datos.
     *
     * @param productoData Objeto DTO con los datos del producto.
     * @return El producto creado.
     */
    public Producto crearProducto(ProductoData productoData) {
        // Validar que el producto no exista previamente por nombre
        if (productoRepository.findByNombre(productoData.getNombre()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un producto con el nombre: " + productoData.getNombre());
        }

        // Mapear de ProductoData a Producto (entidad)
        Producto producto = new Producto();
        producto.setNombre(productoData.getNombre());
        producto.setCategoria(productoData.getCategoria());
        producto.setPrecio(productoData.getPrecio());
        producto.setDescripcion(productoData.getDescripcion());
        producto.setFoto(productoData.getFoto());
        producto.setStock(productoData.getStock());

        // Guardar el producto en la base de datos
        return productoRepository.save(producto);
    }

    /**
     * Listar productos de forma paginada.
     *
     * @param page Número de la página (empezando desde 0).
     * @param size Tamaño de la página (cantidad de productos por página).
     * @return Lista de productos de la página solicitada.
     */
    public List<Producto> listarProductos(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Producto> productoPage = productoRepository.findAll(pageable);
        return productoPage.getContent(); // Retorna los productos de la página actual.
    }

    /**
     * Verificar si hay más productos para la siguiente página.
     *
     * @param page Número de la página actual.
     * @param size Tamaño de la página.
     * @return True si hay más productos, false en caso contrario.
     */
    public boolean hayMasProductos(int page, int size) {
        Pageable pageable = PageRequest.of(page + 1, size); // Verifica la siguiente página.
        return productoRepository.findAll(pageable).hasContent();
    }

    /**
     * Buscar un producto por su ID.
     *
     * @param id ID del producto.
     * @return Producto encontrado o lanza una excepción si no existe.
     */
    public Producto obtenerProductoPorId(Long id) {
        return productoRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("Producto no encontrado con ID: " + id));
    }

    /**
     * Eliminar un producto por su ID.
     *
     * @param id ID del producto a eliminar.
     */
    public void eliminarProducto(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new IllegalArgumentException("No se puede eliminar. Producto no encontrado con ID: " + id);
        }
        productoRepository.deleteById(id);
    }

    /**
     * Actualizar los datos de un producto existente.
     *
     * @param id           ID del producto a actualizar.
     * @param productoData Objeto con los nuevos datos del producto.
     * @return El producto actualizado.
     */
    public Producto actualizarProducto(Long id, ProductoData productoData) {
        Producto productoExistente = productoRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("Producto no encontrado con ID: " + id));

        productoExistente.setNombre(productoData.getNombre());
        productoExistente.setCategoria(productoData.getCategoria());
        productoExistente.setPrecio(productoData.getPrecio());
        productoExistente.setDescripcion(productoData.getDescripcion());
        productoExistente.setFoto(productoData.getFoto());
        productoExistente.setStock(productoData.getStock());

        return productoRepository.save(productoExistente);
    }
}
