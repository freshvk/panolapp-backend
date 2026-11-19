package cl.duoc.panol.prestamos.services;

import java.util.List;

import cl.duoc.panol.prestamos.entities.Prestamo;
import cl.duoc.panol.prestamos.entities.PrestamoRequest;
import cl.duoc.panol.prestamos.security.Identidad;

public interface PrestamoService {

    Prestamo prestar(PrestamoRequest request, Identidad identidad);

    List<Prestamo> listar(String estado, Identidad identidad);

    Prestamo obtener(Long id, Identidad identidad);

    Prestamo devolver(Long id, Identidad identidad);
}
