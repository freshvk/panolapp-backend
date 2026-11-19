package cl.duoc.panol.reportes.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MiResumen {

    private int activos;
    private int vencidos;
    private int devueltos;
}
