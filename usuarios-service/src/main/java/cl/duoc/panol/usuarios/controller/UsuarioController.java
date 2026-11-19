package cl.duoc.panol.usuarios.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.panol.usuarios.entities.RolRequest;
import cl.duoc.panol.usuarios.entities.Usuario;
import cl.duoc.panol.usuarios.entities.UsuarioRequest;
import cl.duoc.panol.usuarios.security.Identidad;
import cl.duoc.panol.usuarios.services.UsuarioService;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/sincronizar")
    public Usuario sincronizar(@RequestBody UsuarioRequest request) {
        return usuarioService.sincronizar(request);
    }

    @GetMapping
    public List<Usuario> listar(
            @RequestHeader(value = "X-Usuario", required = false) String usuario,
            @RequestHeader(value = "X-Rol", required = false) String rol) {
        return usuarioService.listar(Identidad.de(usuario, rol));
    }

    @GetMapping("/{id}")
    public Usuario obtener(
            @PathVariable Long id,
            @RequestHeader(value = "X-Usuario", required = false) String usuario,
            @RequestHeader(value = "X-Rol", required = false) String rol) {
        return usuarioService.obtener(id, Identidad.de(usuario, rol));
    }

    @PutMapping("/{id}/rol")
    public Usuario cambiarRol(
            @PathVariable Long id,
            @RequestBody RolRequest request,
            @RequestHeader(value = "X-Usuario", required = false) String usuario,
            @RequestHeader(value = "X-Rol", required = false) String rol) {
        return usuarioService.cambiarRol(id, request == null ? null : request.getRol(), Identidad.de(usuario, rol));
    }
}
