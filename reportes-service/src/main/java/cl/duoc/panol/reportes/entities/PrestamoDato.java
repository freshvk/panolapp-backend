package cl.duoc.panol.reportes.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrestamoDato {

    private Long id;
    private Long equipoId;
    private String equipoNombre;
    private String usuarioCorreo;
    private int cantidad;
    private long fechaLimite;
    private String estado;
}
