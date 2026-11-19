package cl.duoc.panol.prestamos.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrestamoRequest {

    private Long equipoId;
    private String solicitante;
    private Integer cantidad;
    private Integer dias;
}
