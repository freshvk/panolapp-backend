package cl.duoc.panol.prestamos;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import cl.duoc.panol.prestamos.services.Validaciones;

class ValidacionesTest {

    @Test
    void solicitanteValido() {
        assertNull(Validaciones.solicitante("Juan Pérez"));
    }

    @Test
    void solicitanteVacioOCortoTieneError() {
        assertNotNull(Validaciones.solicitante(" "));
        assertNotNull(Validaciones.solicitante("Jo"));
    }

    @Test
    void cantidadDebeSerPositiva() {
        assertNull(Validaciones.cantidad(1));
        assertNotNull(Validaciones.cantidad(0));
        assertNotNull(Validaciones.cantidad(null));
    }

    @Test
    void diasEntreUnoYTreinta() {
        assertNull(Validaciones.dias(1));
        assertNull(Validaciones.dias(30));
        assertNotNull(Validaciones.dias(0));
        assertNotNull(Validaciones.dias(31));
        assertNotNull(Validaciones.dias(null));
    }

    @Test
    void equipoIdObligatorio() {
        assertNull(Validaciones.equipoId(3L));
        assertNotNull(Validaciones.equipoId(null));
        assertNotNull(Validaciones.equipoId(0L));
    }
}
