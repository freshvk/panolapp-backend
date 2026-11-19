package cl.duoc.panol.prestamos.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Prestamo {

    public static final String ACTIVO = "Activo";
    public static final String DEVUELTO = "Devuelto";

    private Long id;
    private Long equipoId;
    private String equipoNombre;
    private String usuarioCorreo;
    private String solicitante;
    private int cantidad;
    private long fechaPrestamo;
    private long fechaLimite;
    private Long fechaDevolucion;
    private String estado;
}
