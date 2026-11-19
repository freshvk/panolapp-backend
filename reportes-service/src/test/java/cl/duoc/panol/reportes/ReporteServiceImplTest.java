package cl.duoc.panol.reportes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import cl.duoc.panol.reportes.entities.EquipoDato;
import cl.duoc.panol.reportes.entities.MiResumen;
import cl.duoc.panol.reportes.entities.PrestamoDato;
import cl.duoc.panol.reportes.entities.Resumen;
import cl.duoc.panol.reportes.entities.TopEquipo;
import cl.duoc.panol.reportes.exception.ApiException;
import cl.duoc.panol.reportes.security.Identidad;
import cl.duoc.panol.reportes.services.ReporteServiceImpl;

class ReporteServiceImplTest {

    private static final long AHORA = 1_700_000_000_000L;
    private static final long DIA = 86_400_000L;

    private LecturaEnMemoria lectura;
    private ReporteServiceImpl servicio;

    private final Identidad admin = Identidad.de("admin@panol.cl", Identidad.ADMIN);
    private final Identidad ana = Identidad.de("ana@duoc.cl", Identidad.USUARIO);

    @BeforeEach
    void preparar() {
        lectura = new LecturaEnMemoria();
        Clock reloj = Clock.fixed(Instant.ofEpochMilli(AHORA), ZoneOffset.UTC);
        servicio = new ReporteServiceImpl(lectura, reloj);

        lectura.equipos.add(new EquipoDato(1L, "Taladro", 6, 2, 5));
        lectura.equipos.add(new EquipoDato(2L, "Casco", 15, 4, 0));

        lectura.prestamos.add(new PrestamoDato(1L, 1L, "Taladro", "ana@duoc.cl", 3, AHORA + DIA, "Activo"));
        lectura.prestamos.add(new PrestamoDato(2L, 1L, "Taladro", "luis@duoc.cl", 2, AHORA - DIA, "Activo"));
        lectura.prestamos.add(new PrestamoDato(3L, 2L, "Casco", "ana@duoc.cl", 1, AHORA - 5 * DIA, "Devuelto"));
        lectura.prestamos.add(new PrestamoDato(4L, 1L, "Taladro", "ana@duoc.cl", 1, AHORA - 9 * DIA, "Devuelto"));
    }

    @Test
    void resumenCalculaTotales() {
        Resumen r = servicio.resumen(admin);
        assertEquals(2, r.getTotalEquipos());
        assertEquals(21, r.getTotalUnidades());
        assertEquals(5, r.getPrestadas());
        assertEquals(16, r.getDisponibles());
        assertEquals(2, r.getPrestamosActivos());
        assertEquals(1, r.getPrestamosVencidos());
        assertEquals(1, r.getEquiposStockBajo());
    }

    @Test
    void resumenSoloParaAdministrador() {
        ApiException e = assertThrows(ApiException.class, () -> servicio.resumen(ana));
        assertEquals(HttpStatus.FORBIDDEN, e.getEstado());
    }

    @Test
    void topEquiposOrdenaPorCantidadDePrestamos() {
        List<TopEquipo> top = servicio.topEquipos(null, admin);
        assertEquals(2, top.size());
        assertEquals("Taladro", top.get(0).getEquipoNombre());
        assertEquals(3, top.get(0).getPrestamos());
        assertEquals(6, top.get(0).getUnidades());
        assertEquals("Casco", top.get(1).getEquipoNombre());
    }

    @Test
    void topEquiposRespetaElLimite() {
        assertEquals(1, servicio.topEquipos(1, admin).size());
        assertEquals(1, servicio.topEquipos(0, admin).size());
    }

    @Test
    void miResumenSoloCuentaMisPrestamos() {
        MiResumen mio = servicio.miResumen(ana);
        assertEquals(1, mio.getActivos());
        assertEquals(0, mio.getVencidos());
        assertEquals(2, mio.getDevueltos());
    }

    @Test
    void miResumenExigeIdentidad() {
        ApiException e = assertThrows(ApiException.class, () -> servicio.miResumen(Identidad.de(null, null)));
        assertEquals(HttpStatus.UNAUTHORIZED, e.getEstado());
    }
}
