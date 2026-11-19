package cl.duoc.panol.usuarios;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import cl.duoc.panol.usuarios.services.Validaciones;

class ValidacionesTest {

    @Test
    void nombreValidoNoTieneError() {
        assertNull(Validaciones.nombre("Alejandro Lazón"));
    }

    @Test
    void nombreVacioTieneError() {
        assertNotNull(Validaciones.nombre("  "));
    }

    @Test
    void nombreCortoTieneError() {
        assertNotNull(Validaciones.nombre("Al"));
    }

    @Test
    void nombreConNumerosTieneError() {
        assertNotNull(Validaciones.nombre("Ana 123"));
    }

    @Test
    void correoValidoNoTieneError() {
        assertNull(Validaciones.correo("al.lazon@duocuc.cl"));
    }

    @Test
    void correoSinArrobaTieneError() {
        assertNotNull(Validaciones.correo("correo.duocuc.cl"));
    }

    @Test
    void telefonoConEspaciosSeLimpia() {
        assertEquals("+56900000000", Validaciones.limpiarTelefono("+56 9 0000 0000"));
        assertNull(Validaciones.telefono("+56 9 0000 0000"));
    }

    @Test
    void telefonoCortoTieneError() {
        assertNotNull(Validaciones.telefono("1234"));
    }

    @Test
    void rolValidoNoTieneError() {
        assertNull(Validaciones.rol("Administrador"));
        assertNull(Validaciones.rol("Usuario"));
    }

    @Test
    void rolDesconocidoTieneError() {
        assertNotNull(Validaciones.rol("Superadmin"));
    }
}
