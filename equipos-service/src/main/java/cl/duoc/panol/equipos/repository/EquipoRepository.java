package cl.duoc.panol.equipos.repository;

import java.util.List;
import java.util.Optional;

import cl.duoc.panol.equipos.entities.Equipo;

public interface EquipoRepository {

    List<Equipo> listar();

    Optional<Equipo> obtener(Long id);

    Equipo crear(String nombre, String categoria, int cantidadTotal, int stockMinimo);

    Equipo actualizar(Long id, String nombre, String categoria, int cantidadTotal, int stockMinimo);

    void eliminar(Long id);

    boolean estaVacio();
}
