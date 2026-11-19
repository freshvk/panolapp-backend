package cl.duoc.panol.equipos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import cl.duoc.panol.equipos.entities.Equipo;
import cl.duoc.panol.equipos.entities.EquipoRequest;
import cl.duoc.panol.equipos.exception.ApiException;
import cl.duoc.panol.equipos.security.Identidad;
import cl.duoc.panol.equipos.services.EquipoServiceImpl;

class EquipoServiceImplTest {

    private RepositorioEnMemoria repositorio;
    private EquipoServiceImpl servicio;

    private final Identidad admin = Identidad.de("admin@panol.cl", Identidad.ADMIN);
    private final Identidad normal = Identidad.de("ana@duoc.cl", Identidad.USUARIO);

    @BeforeEach
    void preparar() {
        repositorio = new RepositorioEnMemoria();
        servicio = new EquipoServiceImpl(repositorio);
    }

    private EquipoRequest solicitud(String nombre, String categoria, Integer total, Integer minimo) {
        return new EquipoRequest(nombre, categoria, total, minimo);
    }

    @Test
    void administradorCreaEquipo() {
        Equipo creado = servicio.crear(solicitud("  Taladro  ", "Herramientas eléctricas", 6, 2), admin);
        assertEquals("Taladro", creado.getNombre());
        assertEquals(6, creado.getDisponible());
        assertFalse(creado.isStockBajo());
    }

    @Test
    void usuarioNormalNoPuedeCrear() {
        ApiException e = assertThrows(ApiException.class,
            () -> servicio.crear(solicitud("Taladro", "Medición", 6, 2), normal));
        assertEquals(HttpStatus.FORBIDDEN, e.getEstado());
    }

    @Test
    void crearSinIdentidadDevuelve401() {
        ApiException e = assertThrows(ApiException.class,
            () -> servicio.crear(solicitud("Taladro", "Medición", 6, 2), Identidad.de(null, null)));
        assertEquals(HttpStatus.UNAUTHORIZED, e.getEstado());
    }

    @Test
    void crearConDatosInvalidosDevuelve400() {
        ApiException e = assertThrows(ApiException.class,
            () -> servicio.crear(solicitud("Taladro", "Comida", 6, 2), admin));
        assertEquals(HttpStatus.BAD_REQUEST, e.getEstado());
        assertThrows(ApiException.class, () -> servicio.crear(solicitud("Taladro", "Medición", 6, 9), admin));
        assertThrows(ApiException.class, () -> servicio.crear(null, admin));
    }

    @Test
    void listarFiltraPorCategoriaYTextoYOrdenaPorNombre() {
        servicio.crear(solicitud("Multímetro", "Medición", 8, 2), admin);
        servicio.crear(solicitud("Cinta métrica", "Medición", 4, 1), admin);
        servicio.crear(solicitud("Casco", "Seguridad (EPP)", 15, 4), admin);

        List<Equipo> medicion = servicio.listar("Medición", null, normal);
        assertEquals(2, medicion.size());
        assertEquals("Cinta métrica", medicion.get(0).getNombre());

        List<Equipo> porTexto = servicio.listar(null, "CASC", normal);
        assertEquals(1, porTexto.size());
    }

    @Test
    void listarExigeIdentidad() {
        ApiException e = assertThrows(ApiException.class,
            () -> servicio.listar(null, null, Identidad.de("", "")));
        assertEquals(HttpStatus.UNAUTHORIZED, e.getEstado());
    }

    @Test
    void obtenerInexistenteDevuelve404() {
        ApiException e = assertThrows(ApiException.class, () -> servicio.obtener(99L, normal));
        assertEquals(HttpStatus.NOT_FOUND, e.getEstado());
    }

    @Test
    void actualizarNoPermiteBajarDeLoPrestado() {
        Equipo creado = servicio.crear(solicitud("Taladro", "Medición", 6, 2), admin);
        repositorio.fijarPrestadas(creado.getId(), 4);
        ApiException e = assertThrows(ApiException.class,
            () -> servicio.actualizar(creado.getId(), solicitud("Taladro", "Medición", 3, 1), admin));
        assertEquals(HttpStatus.CONFLICT, e.getEstado());

        Equipo ok = servicio.actualizar(creado.getId(), solicitud("Taladro 2", "Medición", 8, 1), admin);
        assertEquals(4, ok.getDisponible());
    }

    @Test
    void eliminarConPrestamosActivosDevuelve409() {
        Equipo creado = servicio.crear(solicitud("Taladro", "Medición", 6, 2), admin);
        repositorio.fijarPrestadas(creado.getId(), 1);
        ApiException e = assertThrows(ApiException.class, () -> servicio.eliminar(creado.getId(), admin));
        assertEquals(HttpStatus.CONFLICT, e.getEstado());
    }

    @Test
    void eliminarSinPrestamosFunciona() {
        Equipo creado = servicio.crear(solicitud("Taladro", "Medición", 6, 2), admin);
        servicio.eliminar(creado.getId(), admin);
        assertTrue(repositorio.estaVacio());
    }

    @Test
    void stockBajoCuandoDisponibleNoSuperaElMinimo() {
        Equipo creado = servicio.crear(solicitud("Taladro", "Medición", 6, 2), admin);
        repositorio.fijarPrestadas(creado.getId(), 4);
        assertTrue(servicio.obtener(creado.getId(), normal).isStockBajo());
    }
}
