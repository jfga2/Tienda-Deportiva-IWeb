package JaySports.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "carrito")
public class Carrito implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "precio_total", nullable = false)
    private Double precioTotal;

    @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductoCarrito> productosCarrito;

    public Carrito() {}

    public Carrito(Usuario usuario, Double precioTotal) {
        this.usuario = usuario;
        this.precioTotal = precioTotal;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Carrito carrito = (Carrito) o;
        return id.equals(carrito.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
