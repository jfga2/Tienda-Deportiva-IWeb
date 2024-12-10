package madstodolist.dto;

import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Email;
import java.util.Date;

public class RegistroData {
    @Email
    private String eMail;
    private String password;
    private String nombre;
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private Date fechaNacimiento;

    // Atributo para marcar si el usuario es administrador
    private boolean administrador;

    public String getEmail() {
        return eMail;
    }

    public void setEmail(String eMail) {
        this.eMail = eMail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public boolean isAdministrador() {
        return administrador;
    }

    public void setAdministrador(boolean administrador) {
        this.administrador = administrador;
    }
}
