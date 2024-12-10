package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.dto.EquipoData;
import madstodolist.dto.UsuarioData;
import madstodolist.service.EquipoService;
import madstodolist.service.EquipoServiceException;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class EquipoController {

    @Autowired
    EquipoService equipoService;

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    ManagerUserSession managerUserSession;

    @GetMapping("/equipos")
    public String showEquipos(Model model) {
        List<EquipoData> equipos = equipoService.findAllOrdenadoPorNombre();
        model.addAttribute("equipos", equipos);
        Long idUsuarioLogeado = managerUserSession.usuarioLogeado();

        Map<Long, Boolean> perteneceAlEquipo = equipos.stream()
                .collect(Collectors.toMap(
                        EquipoData::getId,
                        equipo -> equipoService.usuarioPertenece(equipo.getId(), idUsuarioLogeado)
                ));

        model.addAttribute("perteneceAlEquipo", perteneceAlEquipo);

        if (idUsuarioLogeado != null) {
            UsuarioData usuario = usuarioService.findById(idUsuarioLogeado);
            if (usuario != null) { // Comprobación adicional para evitar NullPointerException
                model.addAttribute("nombreUsuario", usuario.getNombre());
                model.addAttribute("usuarioId", usuario.getId());
            } else {
                model.addAttribute("nombreUsuario", null);
                model.addAttribute("usuarioId", null);
            }
        } else {
            model.addAttribute("nombreUsuario", null);
            model.addAttribute("usuarioId", null);
        }

        model.addAttribute("esAdministrador", managerUserSession.esAdministrador());

        return "ListaEquipos";
    }


    // Nuevo método para mostrar los usuarios en un equipo
    @GetMapping("/equipos/{id}")
    public String showUsuariosEquipo(@PathVariable("id") Long id, Model model) {
        EquipoData equipo = equipoService.recuperarEquipo(id);
        List<UsuarioData> usuarios = equipoService.usuariosEquipo(id);
        model.addAttribute("equipo", equipo);
        model.addAttribute("usuarios", usuarios);

        Long idUsuarioLogeado = managerUserSession.usuarioLogeado();
        if (idUsuarioLogeado != null) {
            UsuarioData usuario = usuarioService.findById(idUsuarioLogeado);
            model.addAttribute("nombreUsuario", usuario.getNombre());
            model.addAttribute("usuarioId", usuario.getId());
            model.addAttribute("esAdministrador", managerUserSession.esAdministrador());
        } else {
            model.addAttribute("nombreUsuario", null);
            model.addAttribute("usuarioId", null);
            model.addAttribute("esAdministrador", false);
        }

        return "usuariosEquipo";
    }

    @PostMapping("/equipos/crear")
    public String crearEquipo(@RequestParam("nombre") String nombre,
            @RequestParam(value = "descripcion", required = false, defaultValue = "") String descripcion,
            RedirectAttributes flash) {
        try {
            EquipoData equipo = equipoService.crearEquipoDescripcion(nombre, descripcion);
            flash.addFlashAttribute("mensaje", "Equipo creado correctamente");
        } catch (EquipoServiceException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/equipos";
    }

    @PostMapping("/equipos/{id}/unirse")
    public String unirseEquipo(@PathVariable("id") Long equipoId, RedirectAttributes flash) {
        Long usuarioId = managerUserSession.usuarioLogeado();
        try {
            equipoService.añadirUsuarioAEquipo(equipoId, usuarioId);
            flash.addFlashAttribute("mensaje", "Te has unido al equipo");
        } catch (EquipoServiceException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/equipos";
    }

    @PostMapping("/equipos/{id}/salir")
    public String salirEquipo(@PathVariable("id") Long equipoId, RedirectAttributes flash) {
        Long usuarioId = managerUserSession.usuarioLogeado();
        try {
            equipoService.eliminarUsuarioDeEquipo(equipoId, usuarioId);
            flash.addFlashAttribute("mensaje", "Has salido del equipo");
        } catch (EquipoServiceException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/equipos";
    }

    @PostMapping("/equipos/{id}/actualizarNombre")
    public String actualizarNombreEquipo(@PathVariable("id") Long equipoId,
                                         @RequestParam("nuevoNombre") String nuevoNombre,
                                         RedirectAttributes flash) {
        try {
            equipoService.actualizarNombreEquipo(equipoId, nuevoNombre);
            flash.addFlashAttribute("mensaje", "El nombre del equipo se ha actualizado");
        } catch (EquipoServiceException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/equipos";
    }

    @PostMapping("/equipos/{id}/eliminar")
    public String eliminarEquipo(@PathVariable("id") Long equipoId, RedirectAttributes flash) {
        try {
            equipoService.eliminarEquipo(equipoId);
            flash.addFlashAttribute("mensaje", "El equipo se ha eliminado correctamente");
        } catch (EquipoServiceException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/equipos";
    }



}
