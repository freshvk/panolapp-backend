package cl.duoc.panol.reportes.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.panol.reportes.entities.MiResumen;
import cl.duoc.panol.reportes.entities.Resumen;
import cl.duoc.panol.reportes.entities.TopEquipo;
import cl.duoc.panol.reportes.security.Identidad;
import cl.duoc.panol.reportes.services.ReporteService;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping("/resumen")
    public Resumen resumen(
            @RequestHeader(value = "X-Usuario", required = false) String usuario,
            @RequestHeader(value = "X-Rol", required = false) String rol) {
        return reporteService.resumen(Identidad.de(usuario, rol));
    }

    @GetMapping("/top-equipos")
    public List<TopEquipo> topEquipos(
            @RequestParam(value = "limite", required = false) Integer limite,
            @RequestHeader(value = "X-Usuario", required = false) String usuario,
            @RequestHeader(value = "X-Rol", required = false) String rol) {
        return reporteService.topEquipos(limite, Identidad.de(usuario, rol));
    }

    @GetMapping("/mi-resumen")
    public MiResumen miResumen(
            @RequestHeader(value = "X-Usuario", required = false) String usuario,
            @RequestHeader(value = "X-Rol", required = false) String rol) {
        return reporteService.miResumen(Identidad.de(usuario, rol));
    }
}
