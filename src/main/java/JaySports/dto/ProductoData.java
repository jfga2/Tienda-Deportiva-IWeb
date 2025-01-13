package JaySports.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

public class ProductoData implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id; // Campo opcional, útil para operaciones como actualización.

    @NotNull(message = "El nombre del producto es obligatorio.")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres.")
    private String nombre;

    @NotNull(message = "La categoría del producto es obligatoria.")
    @Size(min = 2, max = 50, message = "La categoría debe tener entre 2 y 50 caracteres.")
    private String categoria;

    @NotNull(message = "El precio del producto es obligatorio.")
    @Positive(message = "El precio debe ser un número positivo.")
    private Double precio;

    @NotNull(message = "La descripción del producto es obligatoria.")
    @Size(min = 10, max = 500, message = "La descripción debe tener entre 10 y 500 caracteres.")
    private String descripcion;

    @NotNull(message = "La URL de la foto es obligatoria.")
    private String foto;

    @NotNull(message = "El stock del producto es obligatorio.")
    @Positive(message = "El stock debe ser un número positivo.")
    private Integer stock;

    // Constructores
    public ProductoData() {}

    public ProductoData(Long id, String nombre, String categoria, Double precio, String descripcion, String foto, Integer stock) {
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria;
        this.precio = precio;
        this.descripcion = descripcion;
        this.foto = foto;
        this.stock = stock;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    @Override
    public String toString() {
        return "ProductoData{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", categoria='" + categoria + '\'' +
                ", precio=" + precio +
                ", descripcion='" + descripcion + '\'' +
                ", foto='" + foto + '\'' +
                ", stock=" + stock +
                '}';
    }
}
