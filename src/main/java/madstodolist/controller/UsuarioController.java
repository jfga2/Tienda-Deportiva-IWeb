package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.dto.RegistroData;
import madstodolist.dto.UsuarioData;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ManagerUserSession managerUserSession;

    // Atributos comunes a todas las vistas
    @ModelAttribute
    public void addAttributes(Model model) {
        Long idUsuarioLogeado = managerUserSession.usuarioLogeado();
        if (idUsuarioLogeado != null) {
            UsuarioData usuario = usuarioService.findById(idUsuarioLogeado);
            if (usuario != null) {
                model.addAttribute("nombreUsuario", usuario.getNombre());
                model.addAttribute("usuarioId", usuario.getId());
                model.addAttribute("esAdministrador", managerUserSession.esAdministrador());
            }
        } else {
            model.addAttribute("nombreUsuario", null);
            model.addAttribute("usuarioId", null);
            model.addAttribute("esAdministrador", false);
        }
    }

    // Listado de usuarios con paginación - Solo para administradores
    @GetMapping("/registrados")
    public String listaUsuariosRegistrados(@RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "6") int size,
                                           Model model) {
        validarAdministrador();
        Page<UsuarioData> usuariosPage = usuarioService.findAllUsuariosPaginados(page, size);

        model.addAttribute("usuarios", usuariosPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", usuariosPage.getTotalPages());
        model.addAttribute("pageSize", size);

        return "listaUsuariosRegistrados";
    }

    // Formulario para crear un nuevo usuario
    @GetMapping("/registrados/nuevo")
    public String nuevoUsuarioForm(Model model) {
        validarAdministrador();
        model.addAttribute("registroData", new RegistroData());
        return "formRegistro";
    }

    @PostMapping("/registrados/nuevo")
    public String registrarNuevoUsuario(@Valid @ModelAttribute RegistroData registroData, BindingResult result, Model model) {
        validarAdministrador();
        if (result.hasErrors()) {
            return "formRegistro";
        }
        usuarioService.registrar(mapRegistroDataToUsuarioData(registroData));
        return "redirect:/registrados";
    }

    // Formulario para editar un usuario existente
    @GetMapping("/registrados/{id}/editar")
    public String editarUsuarioForm(@PathVariable Long id, Model model) {
        validarAdministrador();
        UsuarioData usuarioData = usuarioService.findById(id);
        if (usuarioData == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
        }
        model.addAttribute("usuarioData", usuarioData);
        return "formEditarUsuario";
    }

    @PostMapping("/registrados/{id}/editar")
    public String actualizarUsuario(@PathVariable Long id, @ModelAttribute UsuarioData usuarioData, BindingResult result) {
        validarAdministrador();
        if (result.hasErrors()) {
            return "formEditarUsuario";
        }
        usuarioService.actualizarUsuario(id, usuarioData);
        return "redirect:/registrados";
    }

    // Eliminar un usuario
    @PostMapping("/registrados/{id}/eliminar")
    public String eliminarUsuario(@PathVariable Long id) {
        validarAdministrador();
        usuarioService.eliminarUsuario(id);
        return "redirect:/registrados";
    }

    // Mostrar la descripción del usuario
    @GetMapping("/registrados/{id}")
    public String descripcionUsuario(@PathVariable Long id, Model model) {
        Long idUsuarioLogeado = managerUserSession.usuarioLogeado();
        if (idUsuarioLogeado == null) {
            return "redirect:/login";
        }
        if (!managerUserSession.esAdministrador() && !idUsuarioLogeado.equals(id)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No tiene permisos para ver esta información.");
        }
        UsuarioData usuario = usuarioService.findById(id);
        if (usuario == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
        }
        model.addAttribute("usuario", usuario);
        return "usuarioDescripcion";
    }

    // Bloquear o desbloquear un usuario
    @PostMapping("/registrados/{id}/bloquear")
    public String bloquearUsuario(@PathVariable Long id, @RequestParam("accion") String accion) {
        validarAdministrador();
        boolean bloquear = "bloquear".equals(accion);
        usuarioService.cambiarEstadoBloqueoUsuario(id, bloquear);
        return "redirect:/registrados";
    }

    // Validar si el usuario actual es administrador
    private void validarAdministrador() {
        Long idUsuarioLogeado = managerUserSession.usuarioLogeado();
        if (idUsuarioLogeado == null || !managerUserSession.esAdministrador()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No tiene permisos para acceder a esta página.");
        }
    }

    // Mapear RegistroData a UsuarioData
    private UsuarioData mapRegistroDataToUsuarioData(RegistroData registroData) {
        UsuarioData usuarioData = new UsuarioData();
        usuarioData.setEmail(registroData.getEmail());
        usuarioData.setNombre(registroData.getNombre());
        usuarioData.setPassword(registroData.getPassword());
        usuarioData.setFechaNacimiento(registroData.getFechaNacimiento());
        usuarioData.setAdministrador(registroData.isAdministrador());
        return usuarioData;
    }
}
