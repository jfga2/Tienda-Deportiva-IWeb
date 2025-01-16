package JaySports.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "carrito")
public class Carrito implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @OneToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "precio_total", nullable = false)
    private Double precioTotal;

    @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductoCarrito> productosCarrito = new ArrayList<>();

    // Constructor vacío
    public Carrito() {}

    // Constructor con parámetros
    public Carrito(Usuario usuario, Double precioTotal) {
        this.usuario = usuario;
        this.precioTotal = precioTotal;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Double getPrecioTotal() {
        return precioTotal;
    }

    public void setPrecioTotal(Double precioTotal) {
        this.precioTotal = precioTotal;
    }

    public List<ProductoCarrito> getProductosCarrito() {
        return productosCarrito;
    }

    public void setProductosCarrito(List<ProductoCarrito> productosCarrito) {
        this.productosCarrito = productosCarrito;
    }

    // Métodos auxiliares para gestionar la relación con ProductoCarrito
    public void agregarProductoCarrito(ProductoCarrito productoCarrito) {
        productosCarrito.add(productoCarrito);
        productoCarrito.setCarrito(this);
    }

    public void eliminarProductoCarrito(ProductoCarrito productoCarrito) {
        productosCarrito.remove(productoCarrito);
        productoCarrito.setCarrito(null);
    }

    public void vaciarProductos() {
        for (ProductoCarrito productoCarrito : new ArrayList<>(productosCarrito)) {
            eliminarProductoCarrito(productoCarrito);
        }
    }

    // Métodos equals y hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Carrito carrito = (Carrito) o;
        return Objects.equals(id, carrito.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Carrito{" +
                "id=" + id +
                ", usuario=" + usuario.getId() +
                ", precioTotal=" + precioTotal +
                '}';
    }
}
