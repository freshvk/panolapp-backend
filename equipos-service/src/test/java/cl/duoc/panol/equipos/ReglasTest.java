package cl.duoc.panol.equipos;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import cl.duoc.panol.equipos.exception.ApiException;
import cl.duoc.panol.equipos.services.Reglas;

class ReglasTest {

    @Test
    void cantidadTotalNoPuedeSerMenorQueLoPrestado() {
        assertThrows(ApiException.class, () -> Reglas.validarCantidadTotal(5, 4));
        assertDoesNotThrow(() -> Reglas.validarCantidadTotal(5, 5));
    }

    @Test
    void soloSePuedeEliminarSinPrestamos() {
        assertThrows(ApiException.class, () -> Reglas.validarEliminable(1));
        assertDoesNotThrow(() -> Reglas.validarEliminable(0));
    }
}
