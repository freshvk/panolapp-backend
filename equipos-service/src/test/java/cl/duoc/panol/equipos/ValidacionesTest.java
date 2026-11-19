package cl.duoc.panol.equipos;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import cl.duoc.panol.equipos.services.Validaciones;

class ValidacionesTest {

    @Test
    void nombreValidoNoTieneError() {
        assertNull(Validaciones.nombre("Taladro percutor"));
    }

    @Test
    void nombreVacioOCortoTieneError() {
        assertNotNull(Validaciones.nombre(""));
        assertNotNull(Validaciones.nombre("ab"));
    }

    @Test
    void nombreMuyLargoTieneError() {
        assertNotNull(Validaciones.nombre("x".repeat(41)));
    }

    @Test
    void categoriaDebeEstarEnLaLista() {
        assertNull(Validaciones.categoria("Medición"));
        assertNotNull(Validaciones.categoria("Comida"));
        assertNotNull(Validaciones.categoria(null));
    }

    @Test
    void cantidadTotalRespetaLimites() {
        assertNull(Validaciones.cantidadTotal(1));
        assertNull(Validaciones.cantidadTotal(9999));
        assertNotNull(Validaciones.cantidadTotal(0));
        assertNotNull(Validaciones.cantidadTotal(10000));
        assertNotNull(Validaciones.cantidadTotal(null));
    }

    @Test
    void stockMinimoNoPuedeSerNegativoNiSuperarElTotal() {
        assertNull(Validaciones.stockMinimo(0, 5));
        assertNull(Validaciones.stockMinimo(5, 5));
        assertNotNull(Validaciones.stockMinimo(-1, 5));
        assertNotNull(Validaciones.stockMinimo(6, 5));
        assertNotNull(Validaciones.stockMinimo(null, 5));
    }
}
