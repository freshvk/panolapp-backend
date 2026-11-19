package cl.duoc.panol.equipos.services;

import java.util.List;

import cl.duoc.panol.equipos.entities.Equipo;
import cl.duoc.panol.equipos.entities.EquipoRequest;
import cl.duoc.panol.equipos.security.Identidad;

public interface EquipoService {

    List<Equipo> listar(String categoria, String consulta, Identidad identidad);

    Equipo obtener(Long id, Identidad identidad);

    Equipo crear(EquipoRequest request, Identidad identidad);

    Equipo actualizar(Long id, EquipoRequest request, Identidad identidad);

    void eliminar(Long id, Identidad identidad);
}
