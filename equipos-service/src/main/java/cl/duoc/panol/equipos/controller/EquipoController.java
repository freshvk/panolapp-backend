package cl.duoc.panol.equipos.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.panol.equipos.entities.Equipo;
import cl.duoc.panol.equipos.entities.EquipoRequest;
import cl.duoc.panol.equipos.security.Identidad;
import cl.duoc.panol.equipos.services.EquipoService;

@RestController
@RequestMapping("/api/equipos")
public class EquipoController {

    private final EquipoService equipoService;

    public EquipoController(EquipoService equipoService) {
        this.equipoService = equipoService;
    }

    @GetMapping
    public List<Equipo> listar(
            @RequestParam(value = "categoria", required = false) String categoria,
            @RequestParam(value = "q", required = false) String consulta,
            @RequestHeader(value = "X-Usuario", required = false) String usuario,
            @RequestHeader(value = "X-Rol", required = false) String rol) {
        return equipoService.listar(categoria, consulta, Identidad.de(usuario, rol));
    }

    @GetMapping("/{id}")
    public Equipo obtener(
            @PathVariable Long id,
            @RequestHeader(value = "X-Usuario", required = false) String usuario,
            @RequestHeader(value = "X-Rol", required = false) String rol) {
        return equipoService.obtener(id, Identidad.de(usuario, rol));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Equipo crear(
            @RequestBody EquipoRequest request,
            @RequestHeader(value = "X-Usuario", required = false) String usuario,
            @RequestHeader(value = "X-Rol", required = false) String rol) {
        return equipoService.crear(request, Identidad.de(usuario, rol));
    }

    @PutMapping("/{id}")
    public Equipo actualizar(
            @PathVariable Long id,
            @RequestBody EquipoRequest request,
            @RequestHeader(value = "X-Usuario", required = false) String usuario,
            @RequestHeader(value = "X-Rol", required = false) String rol) {
        return equipoService.actualizar(id, request, Identidad.de(usuario, rol));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(
            @PathVariable Long id,
            @RequestHeader(value = "X-Usuario", required = false) String usuario,
            @RequestHeader(value = "X-Rol", required = false) String rol) {
        equipoService.eliminar(id, Identidad.de(usuario, rol));
    }
}
