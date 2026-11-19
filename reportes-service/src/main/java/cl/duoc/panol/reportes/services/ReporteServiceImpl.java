package cl.duoc.panol.reportes.services;

import java.time.Clock;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import cl.duoc.panol.reportes.entities.EquipoDato;
import cl.duoc.panol.reportes.entities.MiResumen;
import cl.duoc.panol.reportes.entities.PrestamoDato;
import cl.duoc.panol.reportes.entities.Resumen;
import cl.duoc.panol.reportes.entities.TopEquipo;
import cl.duoc.panol.reportes.repository.LecturaRepository;
import cl.duoc.panol.reportes.security.Identidad;

@Service
public class ReporteServiceImpl implements ReporteService {

    public static final String ACTIVO = "Activo";
    public static final String DEVUELTO = "Devuelto";
    public static final int LIMITE_POR_DEFECTO = 5;
    public static final int LIMITE_MAXIMO = 20;

    private final LecturaRepository repositorio;
    private final Clock reloj;

    public ReporteServiceImpl(LecturaRepository repositorio, Clock reloj) {
        this.repositorio = repositorio;
        this.reloj = reloj;
    }

    @Override
    public Resumen resumen(Identidad identidad) {
        identidad.exigirAdmin();
        List<EquipoDato> equipos = repositorio.equipos();
        List<PrestamoDato> prestamos = repositorio.prestamos();
        long ahora = reloj.millis();

        int unidades = equipos.stream().mapToInt(EquipoDato::getCantidadTotal).sum();
        int prestadas = equipos.stream().mapToInt(EquipoDato::getPrestadas).sum();
        int stockBajo = (int) equipos.stream()
            .filter(e -> e.getCantidadTotal() - e.getPrestadas() <= e.getStockMinimo())
            .count();
        int activos = (int) prestamos.stream().filter(p -> ACTIVO.equals(p.getEstado())).count();
        int vencidos = (int) prestamos.stream().filter(p -> vencido(p, ahora)).count();

        return new Resumen(equipos.size(), unidades, unidades - prestadas, prestadas, activos, vencidos, stockBajo);
    }

    @Override
    public List<TopEquipo> topEquipos(Integer limite, Identidad identidad) {
        identidad.exigirAdmin();
        int max = limite == null ? LIMITE_POR_DEFECTO : Math.max(1, Math.min(limite, LIMITE_MAXIMO));

        Map<String, int[]> acumulado = new LinkedHashMap<>();
        for (PrestamoDato p : repositorio.prestamos()) {
            int[] valores = acumulado.computeIfAbsent(p.getEquipoNombre(), k -> new int[2]);
            valores[0] += 1;
            valores[1] += p.getCantidad();
        }
        return acumulado.entrySet().stream()
            .map(e -> new TopEquipo(e.getKey(), e.getValue()[0], e.getValue()[1]))
            .sorted(Comparator.comparingInt(TopEquipo::getPrestamos).reversed()
                .thenComparing(Comparator.comparingInt(TopEquipo::getUnidades).reversed())
                .thenComparing(TopEquipo::getEquipoNombre, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
            .limit(max)
            .toList();
    }

    @Override
    public MiResumen miResumen(Identidad identidad) {
        identidad.exigirAutenticado();
        List<PrestamoDato> propios = repositorio.prestamosDe(identidad.correo());
        long ahora = reloj.millis();
        int activos = (int) propios.stream().filter(p -> ACTIVO.equals(p.getEstado())).count();
        int vencidos = (int) propios.stream().filter(p -> vencido(p, ahora)).count();
        int devueltos = (int) propios.stream().filter(p -> DEVUELTO.equals(p.getEstado())).count();
        return new MiResumen(activos, vencidos, devueltos);
    }

    private boolean vencido(PrestamoDato p, long ahora) {
        return ACTIVO.equals(p.getEstado()) && ahora > p.getFechaLimite();
    }
}
