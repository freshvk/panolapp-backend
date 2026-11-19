package cl.duoc.panol.usuarios.services;

import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import cl.duoc.panol.usuarios.entities.Usuario;
import cl.duoc.panol.usuarios.entities.UsuarioRequest;
import cl.duoc.panol.usuarios.exception.ApiException;
import cl.duoc.panol.usuarios.repository.UsuarioRepository;
import cl.duoc.panol.usuarios.security.Identidad;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository repositorio;
    private final String correoAdmin;

    public UsuarioServiceImpl(UsuarioRepository repositorio,
                              @Value("${app.admin-correo:admin@panol.cl}") String correoAdmin) {
        this.repositorio = repositorio;
        this.correoAdmin = correoAdmin == null ? "" : correoAdmin.trim().toLowerCase();
    }

    @Override
    public Usuario sincronizar(UsuarioRequest request) {
        if (request == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Faltan los datos del usuario");
        }
        exigirValido(Validaciones.nombre(request.getNombre()));
        exigirValido(Validaciones.correo(request.getCorreo()));
        exigirValido(Validaciones.telefono(request.getTelefono()));

        String nombre = request.getNombre().trim();
        String correo = request.getCorreo().trim().toLowerCase();
        String telefono = Validaciones.limpiarTelefono(request.getTelefono());

        return repositorio.obtenerPorCorreo(correo)
            .map(existente -> {
                existente.setNombre(nombre);
                existente.setTelefono(telefono);
                return repositorio.guardar(existente);
            })
            .orElseGet(() -> {
                String rol = correo.equals(correoAdmin) ? Identidad.ADMIN : Identidad.USUARIO;
                return repositorio.crear(nombre, correo, telefono, rol);
            });
    }

    @Override
    public List<Usuario> listar(Identidad identidad) {
        identidad.exigirAdmin();
        return repositorio.listar().stream()
            .sorted(Comparator.comparing(Usuario::getRol, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(Usuario::getNombre, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
            .toList();
    }

    @Override
    public Usuario obtener(Long id, Identidad identidad) {
        identidad.exigirAutenticado();
        Usuario usuario = repositorio.obtenerPorId(id)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "El usuario no existe"));
        if (!identidad.esAdmin() && !identidad.correo().equals(usuario.getCorreo())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "No puedes ver los datos de otro usuario");
        }
        return usuario;
    }

    @Override
    public Usuario cambiarRol(Long id, String rol, Identidad identidad) {
        identidad.exigirAdmin();
        exigirValido(Validaciones.rol(rol));
        Usuario objetivo = repositorio.obtenerPorId(id)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "El usuario no existe"));
        if (identidad.correo().equals(objetivo.getCorreo())) {
            throw new ApiException(HttpStatus.CONFLICT, "No puedes cambiar tu propio rol");
        }
        objetivo.setRol(rol);
        return repositorio.guardar(objetivo);
    }

    private void exigirValido(String error) {
        if (error != null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, error);
        }
    }
}
