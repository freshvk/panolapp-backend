package cl.duoc.panol.prestamos.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
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

import cl.duoc.panol.prestamos.entities.Prestamo;
import cl.duoc.panol.prestamos.entities.PrestamoRequest;
import cl.duoc.panol.prestamos.security.Identidad;
import cl.duoc.panol.prestamos.services.PrestamoService;

@RestController
@RequestMapping("/api/prestamos")
public class PrestamoController {

    private final PrestamoService prestamoService;

    public PrestamoController(PrestamoService prestamoService) {
        this.prestamoService = prestamoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Prestamo prestar(
            @RequestBody PrestamoRequest request,
            @RequestHeader(value = "X-Usuario", required = false) String usuario,
            @RequestHeader(value = "X-Rol", required = false) String rol) {
        return prestamoService.prestar(request, Identidad.de(usuario, rol));
    }

    @GetMapping
    public List<Prestamo> listar(
            @RequestParam(value = "estado", required = false) String estado,
            @RequestHeader(value = "X-Usuario", required = false) String usuario,
            @RequestHeader(value = "X-Rol", required = false) String rol) {
        return prestamoService.listar(estado, Identidad.de(usuario, rol));
    }

    @GetMapping("/{id}")
    public Prestamo obtener(
            @PathVariable Long id,
            @RequestHeader(value = "X-Usuario", required = false) String usuario,
            @RequestHeader(value = "X-Rol", required = false) String rol) {
        return prestamoService.obtener(id, Identidad.de(usuario, rol));
    }

    @PutMapping("/{id}/devolucion")
    public Prestamo devolver(
            @PathVariable Long id,
            @RequestHeader(value = "X-Usuario", required = false) String usuario,
            @RequestHeader(value = "X-Rol", required = false) String rol) {
        return prestamoService.devolver(id, Identidad.de(usuario, rol));
    }
}
