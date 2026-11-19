package cl.duoc.panol.prestamos.services;

import org.springframework.http.HttpStatus;

import cl.duoc.panol.prestamos.exception.ApiException;

public final class Reglas {

    public static final long MILISEGUNDOS_POR_DIA = 86_400_000L;

    private Reglas() {
    }

    public static void validarStock(int cantidadTotal, int prestadas, int solicitadas) {
        int disponible = cantidadTotal - prestadas;
        if (disponible <= 0) {
            throw new ApiException(HttpStatus.CONFLICT, "No quedan unidades disponibles");
        }
        if (solicitadas > disponible) {
            throw new ApiException(HttpStatus.CONFLICT, "Solo quedan " + disponible + " unidades disponibles");
        }
    }

    public static long fechaLimite(long fechaPrestamo, int dias) {
        return fechaPrestamo + dias * MILISEGUNDOS_POR_DIA;
    }
}
