package cl.duoc.panol.equipos.services;

import org.springframework.http.HttpStatus;

import cl.duoc.panol.equipos.exception.ApiException;

public final class Reglas {

    private Reglas() {
    }

    public static void validarCantidadTotal(int prestadas, int nuevoTotal) {
        if (nuevoTotal < prestadas) {
            throw new ApiException(HttpStatus.CONFLICT,
                "Hay " + prestadas + " unidades prestadas: la cantidad total no puede ser menor");
        }
    }

    public static void validarEliminable(int prestadas) {
        if (prestadas > 0) {
            throw new ApiException(HttpStatus.CONFLICT, "No se puede eliminar: tiene préstamos activos");
        }
    }
}
