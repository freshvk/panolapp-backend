package cl.duoc.panol.prestamos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import cl.duoc.panol.prestamos.entities.Prestamo;
import cl.duoc.panol.prestamos.entities.PrestamoRequest;
import cl.duoc.panol.prestamos.exception.ApiException;
import cl.duoc.panol.prestamos.security.Identidad;
import cl.duoc.panol.prestamos.services.PrestamoServiceImpl;

class PrestamoServiceImplTest {

    private static final long AHORA = 1_700_000_000_000L;

    private RepositorioEnMemoria repositorio;
    private PrestamoServiceImpl servicio;

    private final Identidad admin = Identidad.de("admin@panol.cl", Identidad.ADMIN);
    private final Identidad ana = Identidad.de("ana@duoc.cl", Identidad.USUARIO);
    private final Identidad luis = Identidad.de("luis@duoc.cl", Identidad.USUARIO);

    @BeforeEach
    void preparar() {
        repositorio = new RepositorioEnMemoria();
        repositorio.agregarEquipo(1L, "Taladro", 5);
        Clock reloj = Clock.fixed(Instant.ofEpochMilli(AHORA), ZoneOffset.UTC);
        servicio = new PrestamoServiceImpl(repositorio, reloj);
    }

    private PrestamoRequest solicitud(Long equipoId, String solicitante, Integer cantidad, Integer dias) {
        return new PrestamoRequest(equipoId, solicitante, cantidad, dias);
    }

    @Test
    void prestarCalculaFechasYDescuentaStock() {
        Prestamo prestamo = servicio.prestar(solicitud(1L, " Ana Pérez ", 2, 3), ana);
        assertEquals("Ana Pérez", prestamo.getSolicitante());
        assertEquals("ana@duoc.cl", prestamo.getUsuarioCorreo());
        assertEquals(AHORA, prestamo.getFechaPrestamo());
        assertEquals(AHORA + 3 * 86_400_000L, prestamo.getFechaLimite());
        assertEquals(Prestamo.ACTIVO, prestamo.getEstado());
        assertEquals(2, repositorio.prestadas(1L));
    }

    @Test
    void prestarSinIdentidadDevuelve401() {
        ApiException e = assertThrows(ApiException.class,
            () -> servicio.prestar(solicitud(1L, "Ana Pérez", 1, 1), Identidad.de(null, null)));
        assertEquals(HttpStatus.UNAUTHORIZED, e.getEstado());
    }

    @Test
    void prestarMasDeLoDisponibleDevuelve409() {
        ApiException e = assertThrows(ApiException.class,
            () -> servicio.prestar(solicitud(1L, "Ana Pérez", 6, 1), ana));
        assertEquals(HttpStatus.CONFLICT, e.getEstado());
    }

    @Test
    void prestarConDatosInvalidosDevuelve400() {
        assertThrows(ApiException.class, () -> servicio.prestar(solicitud(1L, "Ana Pérez", 0, 1), ana));
        assertThrows(ApiException.class, () -> servicio.prestar(solicitud(1L, "Ana Pérez", 1, 31), ana));
        assertThrows(ApiException.class, () -> servicio.prestar(solicitud(1L, "", 1, 1), ana));
        assertThrows(ApiException.class, () -> servicio.prestar(null, ana));
    }

    @Test
    void prestarEquipoInexistenteDevuelve404() {
        ApiException e = assertThrows(ApiException.class,
            () -> servicio.prestar(solicitud(9L, "Ana Pérez", 1, 1), ana));
        assertEquals(HttpStatus.NOT_FOUND, e.getEstado());
    }

    @Test
    void usuarioSoloVeSusPrestamosYAdminVeTodos() {
        servicio.prestar(solicitud(1L, "Ana Pérez", 1, 1), ana);
        servicio.prestar(solicitud(1L, "Luis Soto", 1, 1), luis);

        assertEquals(1, servicio.listar(null, ana).size());
        assertEquals(2, servicio.listar(null, admin).size());
    }

    @Test
    void listarFiltraPorEstado() {
        Prestamo uno = servicio.prestar(solicitud(1L, "Ana Pérez", 1, 1), ana);
        servicio.prestar(solicitud(1L, "Ana Pérez", 1, 2), ana);
        servicio.devolver(uno.getId(), admin);

        List<Prestamo> activos = servicio.listar("Activo", admin);
        List<Prestamo> devueltos = servicio.listar("Devuelto", admin);
        assertEquals(1, activos.size());
        assertEquals(1, devueltos.size());
        assertEquals(Prestamo.ACTIVO, servicio.listar(null, admin).get(0).getEstado());
    }

    @Test
    void soloElAdministradorRegistraDevoluciones() {
        Prestamo prestamo = servicio.prestar(solicitud(1L, "Ana Pérez", 2, 1), ana);
        ApiException e = assertThrows(ApiException.class, () -> servicio.devolver(prestamo.getId(), ana));
        assertEquals(HttpStatus.FORBIDDEN, e.getEstado());

        Prestamo devuelto = servicio.devolver(prestamo.getId(), admin);
        assertEquals(Prestamo.DEVUELTO, devuelto.getEstado());
        assertEquals(AHORA, devuelto.getFechaDevolucion());
        assertEquals(0, repositorio.prestadas(1L));
    }

    @Test
    void devolverDosVecesDevuelve409() {
        Prestamo prestamo = servicio.prestar(solicitud(1L, "Ana Pérez", 1, 1), ana);
        servicio.devolver(prestamo.getId(), admin);
        ApiException e = assertThrows(ApiException.class, () -> servicio.devolver(prestamo.getId(), admin));
        assertEquals(HttpStatus.CONFLICT, e.getEstado());
    }

    @Test
    void usuarioNoPuedeVerElPrestamoDeOtro() {
        Prestamo deLuis = servicio.prestar(solicitud(1L, "Luis Soto", 1, 1), luis);
        ApiException e = assertThrows(ApiException.class, () -> servicio.obtener(deLuis.getId(), ana));
        assertEquals(HttpStatus.FORBIDDEN, e.getEstado());
        assertEquals(deLuis.getId(), servicio.obtener(deLuis.getId(), admin).getId());
    }
}
