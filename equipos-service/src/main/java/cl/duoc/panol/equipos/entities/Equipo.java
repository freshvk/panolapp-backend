package cl.duoc.panol.equipos.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Equipo {

    private Long id;
    private String nombre;
    private String categoria;
    private int cantidadTotal;
    private int stockMinimo;
    private int prestadas;

    public int getDisponible() {
        return cantidadTotal - prestadas;
    }

    public boolean isStockBajo() {
        return getDisponible() <= stockMinimo;
    }
}
