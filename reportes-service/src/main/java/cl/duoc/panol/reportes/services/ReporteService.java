package cl.duoc.panol.reportes.services;

import java.util.List;

import cl.duoc.panol.reportes.entities.MiResumen;
import cl.duoc.panol.reportes.entities.Resumen;
import cl.duoc.panol.reportes.entities.TopEquipo;
import cl.duoc.panol.reportes.security.Identidad;

public interface ReporteService {

    Resumen resumen(Identidad identidad);

    List<TopEquipo> topEquipos(Integer limite, Identidad identidad);

    MiResumen miResumen(Identidad identidad);
}
