package cl.duoc.panol.reportes.repository;

import java.util.List;

import cl.duoc.panol.reportes.entities.EquipoDato;
import cl.duoc.panol.reportes.entities.PrestamoDato;

public interface LecturaRepository {

    List<EquipoDato> equipos();

    List<PrestamoDato> prestamos();

    List<PrestamoDato> prestamosDe(String usuarioCorreo);
}
