package cl.duoc.panol.usuarios.repository;

import java.util.List;
import java.util.Optional;

import cl.duoc.panol.usuarios.entities.Usuario;

public interface UsuarioRepository {

    List<Usuario> listar();

    Optional<Usuario> obtenerPorId(Long id);

    Optional<Usuario> obtenerPorCorreo(String correo);

    Usuario crear(String nombre, String correo, String telefono, String rol);

    Usuario guardar(Usuario usuario);
}
