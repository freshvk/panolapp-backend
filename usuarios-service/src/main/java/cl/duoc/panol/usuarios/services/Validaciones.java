package cl.duoc.panol.usuarios.services;

import java.util.regex.Pattern;

import cl.duoc.panol.usuarios.security.Identidad;

public final class Validaciones {

    private static final Pattern CORREO = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern TELEFONO = Pattern.compile("^\\+?[0-9]{8,12}$");

    private Validaciones() {
    }

    public static String limpiarTelefono(String valor) {
        return valor == null ? "" : valor.replace(" ", "").trim();
    }

    public static String nombre(String valor) {
        if (valor == null || valor.isBlank()) {
            return "El nombre es obligatorio";
        }
        String limpio = valor.trim();
        if (limpio.length() < 3) {
            return "El nombre debe tener al menos 3 caracteres";
        }
        for (char c : limpio.toCharArray()) {
            if (!Character.isLetter(c) && c != ' ') {
                return "El nombre solo admite letras y espacios";
            }
        }
        return null;
    }

    public static String correo(String valor) {
        if (valor == null || valor.isBlank()) {
            return "El correo es obligatorio";
        }
        if (!CORREO.matcher(valor.trim()).matches()) {
            return "El correo no es válido";
        }
        return null;
    }

    public static String telefono(String valor) {
        String limpio = limpiarTelefono(valor);
        if (limpio.isEmpty()) {
            return "El teléfono es obligatorio";
        }
        if (!TELEFONO.matcher(limpio).matches()) {
            return "El teléfono debe tener de 8 a 12 dígitos";
        }
        return null;
    }

    public static String rol(String valor) {
        if (!Identidad.ADMIN.equals(valor) && !Identidad.USUARIO.equals(valor)) {
            return "El rol debe ser Administrador o Usuario";
        }
        return null;
    }
}
