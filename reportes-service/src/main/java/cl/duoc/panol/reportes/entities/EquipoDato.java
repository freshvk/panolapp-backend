package cl.duoc.panol.reportes.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EquipoDato {

    private Long id;
    private String nombre;
    private int cantidadTotal;
    private int stockMinimo;
    private int prestadas;
}
