package cl.duoc.panol.prestamos.services;

import java.time.Clock;
import java.util.Comparator;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import cl.duoc.panol.prestamos.entities.Prestamo;
import cl.duoc.panol.prestamos.entities.PrestamoRequest;
import cl.duoc.panol.prestamos.exception.ApiException;
import cl.duoc.panol.prestamos.repository.PrestamoRepository;
import cl.duoc.panol.prestamos.security.Identidad;

@Service
public class PrestamoServiceImpl implements PrestamoService {

    private final PrestamoRepository repositorio;
    private final Clock reloj;

    public PrestamoServiceImpl(PrestamoRepository repositorio, Clock reloj) {
        this.repositorio = repositorio;
        this.reloj = reloj;
    }

    @Override
    public Prestamo prestar(PrestamoRequest request, Identidad identidad) {
        identidad.exigirAutenticado();
        if (request == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Faltan los datos del préstamo");
        }
        exigirValido(Validaciones.equipoId(request.getEquipoId()));
        exigirValido(Validaciones.solicitante(request.getSolicitante()));
        exigirValido(Validaciones.cantidad(request.getCantidad()));
        exigirValido(Validaciones.dias(request.getDias()));

        long ahora = reloj.millis();
        return repositorio.crear(
            request.getEquipoId(),
            request.getSolicitante().trim(),
            identidad.correo(),
            request.getCantidad(),
            ahora,
            Reglas.fechaLimite(ahora, request.getDias()));
    }

    @Override
    public List<Prestamo> listar(String estado, Identidad identidad) {
        identidad.exigirAutenticado();
        List<Prestamo> base = identidad.esAdmin()
            ? repositorio.listar()
            : repositorio.listarPorUsuario(identidad.correo());
        return base.stream()
            .filter(p -> estado == null || estado.isBlank() || estado.equalsIgnoreCase(p.getEstado()))
            .sorted(Comparator
                .comparing((Prestamo p) -> !Prestamo.ACTIVO.equals(p.getEstado()))
                .thenComparingLong(Prestamo::getFechaLimite))
            .toList();
    }

    @Override
    public Prestamo obtener(Long id, Identidad identidad) {
        identidad.exigirAutenticado();
        Prestamo prestamo = repositorio.obtener(id)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "El préstamo no existe"));
        if (!identidad.esAdmin() && !identidad.correo().equals(prestamo.getUsuarioCorreo())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "No puedes ver los préstamos de otro usuario");
        }
        return prestamo;
    }

    @Override
    public Prestamo devolver(Long id, Identidad identidad) {
        identidad.exigirAdmin();
        return repositorio.devolver(id, reloj.millis());
    }

    private void exigirValido(String error) {
        if (error != null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, error);
        }
    }
}
