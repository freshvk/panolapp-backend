package cl.duoc.panol.equipos.services;

import java.util.Comparator;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import cl.duoc.panol.equipos.entities.Equipo;
import cl.duoc.panol.equipos.entities.EquipoRequest;
import cl.duoc.panol.equipos.exception.ApiException;
import cl.duoc.panol.equipos.repository.EquipoRepository;
import cl.duoc.panol.equipos.security.Identidad;

@Service
public class EquipoServiceImpl implements EquipoService {

    private final EquipoRepository repositorio;

    public EquipoServiceImpl(EquipoRepository repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public List<Equipo> listar(String categoria, String consulta, Identidad identidad) {
        identidad.exigirAutenticado();
        String texto = consulta == null ? "" : consulta.trim().toLowerCase();
        return repositorio.listar().stream()
            .filter(e -> categoria == null || categoria.isBlank() || categoria.equals(e.getCategoria()))
            .filter(e -> texto.isEmpty() || e.getNombre().toLowerCase().contains(texto))
            .sorted(Comparator.comparing(Equipo::getNombre, String.CASE_INSENSITIVE_ORDER))
            .toList();
    }

    @Override
    public Equipo obtener(Long id, Identidad identidad) {
        identidad.exigirAutenticado();
        return repositorio.obtener(id)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "El equipo no existe"));
    }

    @Override
    public Equipo crear(EquipoRequest request, Identidad identidad) {
        identidad.exigirAdmin();
        validar(request);
        return repositorio.crear(
            request.getNombre().trim(),
            request.getCategoria(),
            request.getCantidadTotal(),
            request.getStockMinimo());
    }

    @Override
    public Equipo actualizar(Long id, EquipoRequest request, Identidad identidad) {
        identidad.exigirAdmin();
        validar(request);
        return repositorio.actualizar(
            id,
            request.getNombre().trim(),
            request.getCategoria(),
            request.getCantidadTotal(),
            request.getStockMinimo());
    }

    @Override
    public void eliminar(Long id, Identidad identidad) {
        identidad.exigirAdmin();
        repositorio.eliminar(id);
    }

    private void validar(EquipoRequest request) {
        if (request == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Faltan los datos del equipo");
        }
        exigirValido(Validaciones.nombre(request.getNombre()));
        exigirValido(Validaciones.categoria(request.getCategoria()));
        exigirValido(Validaciones.cantidadTotal(request.getCantidadTotal()));
        exigirValido(Validaciones.stockMinimo(request.getStockMinimo(), request.getCantidadTotal()));
    }

    private void exigirValido(String error) {
        if (error != null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, error);
        }
    }
}
