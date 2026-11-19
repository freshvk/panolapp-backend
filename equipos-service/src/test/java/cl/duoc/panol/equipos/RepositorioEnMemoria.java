package cl.duoc.panol.equipos;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;

import cl.duoc.panol.equipos.entities.Equipo;
import cl.duoc.panol.equipos.exception.ApiException;
import cl.duoc.panol.equipos.repository.EquipoRepository;
import cl.duoc.panol.equipos.services.Reglas;

public class RepositorioEnMemoria implements EquipoRepository {

    private final Map<Long, Equipo> datos = new LinkedHashMap<>();
    private long contador = 0;

    public void fijarPrestadas(Long id, int prestadas) {
        datos.get(id).setPrestadas(prestadas);
    }

    @Override
    public List<Equipo> listar() {
        return new ArrayList<>(datos.values());
    }

    @Override
    public Optional<Equipo> obtener(Long id) {
        return Optional.ofNullable(datos.get(id));
    }

    @Override
    public Equipo crear(String nombre, String categoria, int cantidadTotal, int stockMinimo) {
        contador++;
        Equipo equipo = new Equipo(contador, nombre, categoria, cantidadTotal, stockMinimo, 0);
        datos.put(contador, equipo);
        return equipo;
    }

    @Override
    public Equipo actualizar(Long id, String nombre, String categoria, int cantidadTotal, int stockMinimo) {
        Equipo equipo = datos.get(id);
        if (equipo == null) {
            throw new ApiException(HttpStatus.NOT_FOUND, "El equipo no existe");
        }
        Reglas.validarCantidadTotal(equipo.getPrestadas(), cantidadTotal);
        equipo.setNombre(nombre);
        equipo.setCategoria(categoria);
        equipo.setCantidadTotal(cantidadTotal);
        equipo.setStockMinimo(stockMinimo);
        return equipo;
    }

    @Override
    public void eliminar(Long id) {
        Equipo equipo = datos.get(id);
        if (equipo == null) {
            throw new ApiException(HttpStatus.NOT_FOUND, "El equipo no existe");
        }
        Reglas.validarEliminable(equipo.getPrestadas());
        datos.remove(id);
    }

    @Override
    public boolean estaVacio() {
        return datos.isEmpty();
    }
}
