package cl.duoc.panol.equipos.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EquipoRequest {

    private String nombre;
    private String categoria;
    private Integer cantidadTotal;
    private Integer stockMinimo;
}
