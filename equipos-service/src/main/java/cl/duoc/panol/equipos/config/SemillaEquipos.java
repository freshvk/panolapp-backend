package cl.duoc.panol.equipos.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import cl.duoc.panol.equipos.repository.EquipoRepository;

@Component
public class SemillaEquipos implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SemillaEquipos.class);

    private final EquipoRepository repositorio;

    public SemillaEquipos(EquipoRepository repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public void run(String... args) {
        try {
            if (!repositorio.estaVacio()) {
                return;
            }
            repositorio.crear("Taladro percutor 650W", "Herramientas eléctricas", 6, 2);
            repositorio.crear("Set de llaves combinadas", "Herramientas manuales", 10, 3);
            repositorio.crear("Multímetro digital", "Medición", 8, 2);
            repositorio.crear("Casco de seguridad", "Seguridad (EPP)", 15, 4);
            repositorio.crear("Notebook de préstamo", "Computación", 5, 1);
            log.info("Se cargaron los equipos de ejemplo");
        } catch (RuntimeException e) {
            log.warn("No se pudieron cargar los equipos de ejemplo: {}", e.getMessage());
        }
    }
}
