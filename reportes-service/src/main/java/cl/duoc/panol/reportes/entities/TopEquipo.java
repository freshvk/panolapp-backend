package cl.duoc.panol.reportes.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopEquipo {

    private String equipoNombre;
    private int prestamos;
    private int unidades;
}
