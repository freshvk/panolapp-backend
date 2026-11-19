package cl.duoc.panol.equipos.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SaludController {

    @GetMapping("/health")
    public Map<String, String> salud() {
        return Map.of("servicio", "equipos-service", "estado", "ok");
    }
}
