package cl.duoc.panol.prestamos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;

import cl.duoc.panol.prestamos.entities.Prestamo;
import cl.duoc.panol.prestamos.exception.ApiException;
import cl.duoc.panol.prestamos.repository.PrestamoRepository;
import cl.duoc.panol.prestamos.services.Reglas;

public class RepositorioEnMemoria implements PrestamoRepository {

    private final Map<Long, Prestamo> datos = new LinkedHashMap<>();
    private final Map<Long, int[]> equipos = new HashMap<>();
    private final Map<Long, String> nombres = new HashMap<>();
    private long contador = 0;

    public void agregarEquipo(Long id, String nombre, int total) {
        equipos.put(id, new int[] {total, 0});
        nombres.put(id, nombre);
    }

    public int prestadas(Long equipoId) {
        return equipos.get(equipoId)[1];
    }

    @Override
    public Prestamo crear(Long equipoId, String solicitante, String usuarioCorreo, int cantidad,
                          long fechaPrestamo, long fechaLimite) {
        int[] stock = equipos.get(equipoId);
        if (stock == null) {
            throw new ApiException(HttpStatus.NOT_FOUND, "El equipo no existe");
        }
        Reglas.validarStock(stock[0], stock[1], cantidad);
        stock[1] += cantidad;
        contador++;
        Prestamo prestamo = new Prestamo(contador, equipoId, nombres.get(equipoId), usuarioCorreo, solicitante,
            cantidad, fechaPrestamo, fechaLimite, null, Prestamo.ACTIVO);
        datos.put(contador, prestamo);
        return prestamo;
    }

    @Override
    public List<Prestamo> listar() {
        return new ArrayList<>(datos.values());
    }

    @Override
    public List<Prestamo> listarPorUsuario(String usuarioCorreo) {
        return datos.values().stream().filter(p -> p.getUsuarioCorreo().equals(usuarioCorreo)).toList();
    }

    @Override
    public Optional<Prestamo> obtener(Long id) {
        return Optional.ofNullable(datos.get(id));
    }

    @Override
    public Prestamo devolver(Long id, long fechaDevolucion) {
        Prestamo prestamo = datos.get(id);
        if (prestamo == null) {
            throw new ApiException(HttpStatus.NOT_FOUND, "El préstamo no existe");
        }
        if (!Prestamo.ACTIVO.equals(prestamo.getEstado())) {
            throw new ApiException(HttpStatus.CONFLICT, "El préstamo ya fue devuelto");
        }
        prestamo.setEstado(Prestamo.DEVUELTO);
        prestamo.setFechaDevolucion(fechaDevolucion);
        equipos.get(prestamo.getEquipoId())[1] -= prestamo.getCantidad();
        return prestamo;
    }
}
