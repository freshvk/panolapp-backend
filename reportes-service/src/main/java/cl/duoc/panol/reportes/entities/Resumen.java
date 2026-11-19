package cl.duoc.panol.reportes.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Resumen {

    private int totalEquipos;
    private int totalUnidades;
    private int disponibles;
    private int prestadas;
    private int prestamosActivos;
    private int prestamosVencidos;
    private int equiposStockBajo;
}
