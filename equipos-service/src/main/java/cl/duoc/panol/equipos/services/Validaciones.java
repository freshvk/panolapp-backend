package cl.duoc.panol.equipos.services;

import java.util.List;

public final class Validaciones {

    public static final List<String> CATEGORIAS = List.of(
        "Herramientas eléctricas",
        "Herramientas manuales",
        "Medición",
        "Seguridad (EPP)",
        "Computación",
        "Otros");

    public static final int MAXIMO_UNIDADES = 9999;

    private Validaciones() {
    }

    public static String nombre(String valor) {
        if (valor == null || valor.isBlank()) {
            return "El nombre del equipo es obligatorio";
        }
        int largo = valor.trim().length();
        if (largo < 3) {
            return "El nombre debe tener al menos 3 caracteres";
        }
        if (largo > 40) {
            return "El nombre admite como máximo 40 caracteres";
        }
        return null;
    }

    public static String categoria(String valor) {
        if (valor == null || !CATEGORIAS.contains(valor)) {
            return "La categoría no es válida";
        }
        return null;
    }

    public static String cantidadTotal(Integer valor) {
        if (valor == null) {
            return "La cantidad total es obligatoria";
        }
        if (valor < 1) {
            return "La cantidad total debe ser al menos 1";
        }
        if (valor > MAXIMO_UNIDADES) {
            return "La cantidad total admite como máximo " + MAXIMO_UNIDADES;
        }
        return null;
    }

    public static String stockMinimo(Integer valor, Integer cantidadTotal) {
        if (valor == null) {
            return "El stock mínimo es obligatorio";
        }
        if (valor < 0) {
            return "El stock mínimo no puede ser negativo";
        }
        if (cantidadTotal != null && valor > cantidadTotal) {
            return "El stock mínimo no puede superar la cantidad total";
        }
        return null;
    }
}
