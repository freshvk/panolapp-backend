package cl.duoc.panol.prestamos.services;

public final class Validaciones {

    public static final int MAXIMO_DIAS = 30;
    public static final int MAXIMO_UNIDADES = 9999;

    private Validaciones() {
    }

    public static String equipoId(Long valor) {
        return valor == null || valor < 1 ? "Debes indicar el equipo" : null;
    }

    public static String solicitante(String valor) {
        if (valor == null || valor.isBlank()) {
            return "Indica quién retira el equipo";
        }
        if (valor.trim().length() < 3) {
            return "El solicitante debe tener al menos 3 caracteres";
        }
        return null;
    }

    public static String cantidad(Integer valor) {
        if (valor == null) {
            return "La cantidad es obligatoria";
        }
        if (valor < 1) {
            return "La cantidad debe ser al menos 1";
        }
        if (valor > MAXIMO_UNIDADES) {
            return "La cantidad admite como máximo " + MAXIMO_UNIDADES;
        }
        return null;
    }

    public static String dias(Integer valor) {
        if (valor == null) {
            return "Los días de préstamo son obligatorios";
        }
        if (valor < 1) {
            return "El préstamo debe ser de al menos 1 día";
        }
        if (valor > MAXIMO_DIAS) {
            return "El préstamo admite como máximo " + MAXIMO_DIAS + " días";
        }
        return null;
    }
}
