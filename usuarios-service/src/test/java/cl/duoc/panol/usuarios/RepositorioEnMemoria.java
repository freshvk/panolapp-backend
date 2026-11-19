package cl.duoc.panol.usuarios;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import cl.duoc.panol.usuarios.entities.Usuario;
import cl.duoc.panol.usuarios.repository.UsuarioRepository;

public class RepositorioEnMemoria implements UsuarioRepository {

    private final Map<Long, Usuario> datos = new LinkedHashMap<>();
    private long contador = 0;

    @Override
    public List<Usuario> listar() {
        return new ArrayList<>(datos.values());
    }

    @Override
    public Optional<Usuario> obtenerPorId(Long id) {
        return Optional.ofNullable(datos.get(id));
    }

    @Override
    public Optional<Usuario> obtenerPorCorreo(String correo) {
        return datos.values().stream().filter(u -> u.getCorreo().equals(correo)).findFirst();
    }

    @Override
    public Usuario crear(String nombre, String correo, String telefono, String rol) {
        contador++;
        Usuario usuario = new Usuario(contador, nombre, correo, telefono, rol);
        datos.put(contador, usuario);
        return usuario;
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        datos.put(usuario.getId(), usuario);
        return usuario;
    }
}
