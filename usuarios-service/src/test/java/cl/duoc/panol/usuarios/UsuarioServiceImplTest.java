package cl.duoc.panol.usuarios;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import cl.duoc.panol.usuarios.entities.Usuario;
import cl.duoc.panol.usuarios.entities.UsuarioRequest;
import cl.duoc.panol.usuarios.exception.ApiException;
import cl.duoc.panol.usuarios.security.Identidad;
import cl.duoc.panol.usuarios.services.UsuarioServiceImpl;

class UsuarioServiceImplTest {

    private RepositorioEnMemoria repositorio;
    private UsuarioServiceImpl servicio;

    private final Identidad admin = Identidad.de("admin@panol.cl", Identidad.ADMIN);
    private final Identidad normal = Identidad.de("ana@duoc.cl", Identidad.USUARIO);

    @BeforeEach
    void preparar() {
        repositorio = new RepositorioEnMemoria();
        servicio = new UsuarioServiceImpl(repositorio, "admin@panol.cl");
    }

    private UsuarioRequest solicitud(String nombre, String correo, String telefono) {
        return new UsuarioRequest(nombre, correo, telefono);
    }

    @Test
    void sincronizarCreaUsuarioConRolUsuario() {
        Usuario creado = servicio.sincronizar(solicitud("Ana Pérez", "Ana@Duoc.cl", "+56912345678"));
        assertEquals("ana@duoc.cl", creado.getCorreo());
        assertEquals(Identidad.USUARIO, creado.getRol());
    }

    @Test
    void sincronizarAsignaRolAdministradorAlCorreoAdmin() {
        Usuario creado = servicio.sincronizar(solicitud("Administrador del Pañol", "admin@panol.cl", "+56 9 0000 0000"));
        assertEquals(Identidad.ADMIN, creado.getRol());
        assertEquals("+56900000000", creado.getTelefono());
    }

    @Test
    void sincronizarNoCambiaElRolDeUnUsuarioExistente() {
        Usuario creado = servicio.sincronizar(solicitud("Ana Pérez", "ana@duoc.cl", "+56912345678"));
        servicio.cambiarRol(creado.getId(), Identidad.ADMIN, admin);
        Usuario actualizado = servicio.sincronizar(solicitud("Ana María Pérez", "ana@duoc.cl", "+56987654321"));
        assertEquals(Identidad.ADMIN, actualizado.getRol());
        assertEquals("Ana María Pérez", actualizado.getNombre());
        assertEquals(1, repositorio.listar().size());
    }

    @Test
    void sincronizarConDatosInvalidosDevuelve400() {
        ApiException e = assertThrows(ApiException.class,
            () -> servicio.sincronizar(solicitud("Ana", "correo-malo", "+56912345678")));
        assertEquals(HttpStatus.BAD_REQUEST, e.getEstado());
    }

    @Test
    void listarSoloParaAdministrador() {
        servicio.sincronizar(solicitud("Ana Pérez", "ana@duoc.cl", "+56912345678"));
        ApiException e = assertThrows(ApiException.class, () -> servicio.listar(normal));
        assertEquals(HttpStatus.FORBIDDEN, e.getEstado());
        assertEquals(1, servicio.listar(admin).size());
    }

    @Test
    void listarSinIdentidadDevuelve401() {
        ApiException e = assertThrows(ApiException.class, () -> servicio.listar(Identidad.de(null, null)));
        assertEquals(HttpStatus.UNAUTHORIZED, e.getEstado());
    }

    @Test
    void usuarioNoPuedeVerDatosDeOtro() {
        Usuario otro = servicio.sincronizar(solicitud("Luis Soto", "luis@duoc.cl", "+56911111111"));
        ApiException e = assertThrows(ApiException.class, () -> servicio.obtener(otro.getId(), normal));
        assertEquals(HttpStatus.FORBIDDEN, e.getEstado());
    }

    @Test
    void cambiarRolSoloAdministradorYNoElPropio() {
        Usuario yo = servicio.sincronizar(solicitud("Administrador del Pañol", "admin@panol.cl", "+56900000000"));
        Usuario ana = servicio.sincronizar(solicitud("Ana Pérez", "ana@duoc.cl", "+56912345678"));

        assertThrows(ApiException.class, () -> servicio.cambiarRol(ana.getId(), Identidad.ADMIN, normal));

        ApiException propio = assertThrows(ApiException.class,
            () -> servicio.cambiarRol(yo.getId(), Identidad.USUARIO, admin));
        assertEquals(HttpStatus.CONFLICT, propio.getEstado());

        Usuario cambiado = servicio.cambiarRol(ana.getId(), Identidad.ADMIN, admin);
        assertEquals(Identidad.ADMIN, cambiado.getRol());
    }

    @Test
    void cambiarRolConValorInvalidoDevuelve400() {
        Usuario ana = servicio.sincronizar(solicitud("Ana Pérez", "ana@duoc.cl", "+56912345678"));
        ApiException e = assertThrows(ApiException.class, () -> servicio.cambiarRol(ana.getId(), "Root", admin));
        assertEquals(HttpStatus.BAD_REQUEST, e.getEstado());
    }
}
