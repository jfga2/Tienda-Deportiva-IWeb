package madstodolist.dto;

import java.util.Objects;

public class EquipoData {
    private Long id;
    private String nombre;
    private String descripcion;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion){
        this.descripcion = descripcion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EquipoData equipo = (EquipoData) o;
        if (id != null && equipo.id != null)
            return Objects.equals(id, equipo.id);
        return nombre.equals(equipo.nombre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre);
    }
}
