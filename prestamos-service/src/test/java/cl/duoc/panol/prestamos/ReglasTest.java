package cl.duoc.panol.prestamos;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import cl.duoc.panol.prestamos.exception.ApiException;
import cl.duoc.panol.prestamos.services.Reglas;

class ReglasTest {

    @Test
    void permiteHastaElDisponible() {
        assertDoesNotThrow(() -> Reglas.validarStock(10, 7, 3));
    }

    @Test
    void rechazaMasDeLoDisponible() {
        ApiException e = assertThrows(ApiException.class, () -> Reglas.validarStock(10, 7, 4));
        assertEquals(HttpStatus.CONFLICT, e.getEstado());
        assertEquals("Solo quedan 3 unidades disponibles", e.getMessage());
    }

    @Test
    void rechazaCuandoNoQuedaNada() {
        ApiException e = assertThrows(ApiException.class, () -> Reglas.validarStock(5, 5, 1));
        assertEquals("No quedan unidades disponibles", e.getMessage());
    }

    @Test
    void fechaLimiteSumaLosDias() {
        assertEquals(1_000L + 3 * 86_400_000L, Reglas.fechaLimite(1_000L, 3));
    }
}
