package cl.duoc.panol.prestamos.repository;

import java.util.List;
import java.util.Optional;

import cl.duoc.panol.prestamos.entities.Prestamo;

public interface PrestamoRepository {

    Prestamo crear(Long equipoId, String solicitante, String usuarioCorreo, int cantidad,
                   long fechaPrestamo, long fechaLimite);

    List<Prestamo> listar();

    List<Prestamo> listarPorUsuario(String usuarioCorreo);

    Optional<Prestamo> obtener(Long id);

    Prestamo devolver(Long id, long fechaDevolucion);
}
