package cl.duoc.panol.usuarios.services;

import java.util.List;

import cl.duoc.panol.usuarios.entities.Usuario;
import cl.duoc.panol.usuarios.entities.UsuarioRequest;
import cl.duoc.panol.usuarios.security.Identidad;

public interface UsuarioService {

    Usuario sincronizar(UsuarioRequest request);

    List<Usuario> listar(Identidad identidad);

    Usuario obtener(Long id, Identidad identidad);

    Usuario cambiarRol(Long id, String rol, Identidad identidad);
}
