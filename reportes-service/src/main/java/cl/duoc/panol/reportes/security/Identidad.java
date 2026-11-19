package cl.duoc.panol.reportes.security;

import org.springframework.http.HttpStatus;

import cl.duoc.panol.reportes.exception.ApiException;

public record Identidad(String correo, String rol) {

    public static final String ADMIN = "Administrador";
    public static final String USUARIO = "Usuario";

    public static Identidad de(String correo, String rol) {
        String c = correo == null ? "" : correo.trim().toLowerCase();
        String r = rol == null ? "" : rol.trim();
        return new Identidad(c, r);
    }

    public boolean autenticado() {
        return !correo.isBlank();
    }

    public boolean esAdmin() {
        return ADMIN.equals(rol);
    }

    public void exigirAutenticado() {
        if (!autenticado()) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Debes iniciar sesión");
        }
    }

    public void exigirAdmin() {
        exigirAutenticado();
        if (!esAdmin()) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Solo el administrador puede realizar esta acción");
        }
    }
}
