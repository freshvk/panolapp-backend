package cl.duoc.panol.reportes;

import java.util.ArrayList;
import java.util.List;

import cl.duoc.panol.reportes.entities.EquipoDato;
import cl.duoc.panol.reportes.entities.PrestamoDato;
import cl.duoc.panol.reportes.repository.LecturaRepository;

public class LecturaEnMemoria implements LecturaRepository {

    public final List<EquipoDato> equipos = new ArrayList<>();
    public final List<PrestamoDato> prestamos = new ArrayList<>();

    @Override
    public List<EquipoDato> equipos() {
        return equipos;
    }

    @Override
    public List<PrestamoDato> prestamos() {
        return prestamos;
    }

    @Override
    public List<PrestamoDato> prestamosDe(String usuarioCorreo) {
        return prestamos.stream().filter(p -> p.getUsuarioCorreo().equals(usuarioCorreo)).toList();
    }
}
