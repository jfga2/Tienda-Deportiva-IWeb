package JaySports.authentication;

import JaySports.model.Usuario;
import JaySports.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpSession;

@Component
public class ManagerUserSession {

    @Autowired
    HttpSession session;

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Logear un usuario, guardando su ID, nombre y si es administrador en la sesión.
     *
     * @param idUsuario       ID del usuario logueado.
     * @param nombreUsuario   Nombre del usuario logueado.
     * @param esAdministrador Indica si el usuario es administrador.
     */
    public void logearUsuario(Long idUsuario, String nombreUsuario, boolean esAdministrador) {
        session.setAttribute("idUsuarioLogeado", idUsuario);
        session.setAttribute("nombreUsuarioLogeado", nombreUsuario);
        session.setAttribute("esAdministrador", esAdministrador);
    }

    /**
     * Obtener el ID del usuario logueado desde la sesión.
     *
     * @return ID del usuario logueado.
     */
    public Long usuarioLogeado() {
        return (Long) session.getAttribute("idUsuarioLogeado");
    }

    /**
     * Verificar si el usuario logueado es administrador.
     *
     * @return True si el usuario es administrador, false en caso contrario.
     */
    public boolean esAdministrador() {
        Boolean esAdmin = (Boolean) session.getAttribute("esAdministrador");
        return esAdmin != null && esAdmin;
    }

    /**
     * Obtener el nombre del usuario logueado desde la sesión.
     *
     * @return Nombre del usuario logueado.
     */
    public String obtenerNombreUsuario() {
        return (String) session.getAttribute("nombreUsuarioLogeado");
    }

    /**
     * Cerrar sesión del usuario, eliminando sus datos de la sesión.
     */
    public void logout() {
        session.setAttribute("idUsuarioLogeado", null);
        session.setAttribute("nombreUsuarioLogeado", null);
        session.setAttribute("esAdministrador", null);
    }

    /**
     * Obtener el usuario logueado utilizando su ID almacenado en la sesión.
     *
     * @return Usuario logueado o null si no está logueado.
     */
    public Usuario obtenerUsuarioLogeado() {
        Long idUsuario = usuarioLogeado();
        if (idUsuario == null) {
            return null;
        }
        return usuarioService.obtenerUsuarioPorId(idUsuario);
    }
}
