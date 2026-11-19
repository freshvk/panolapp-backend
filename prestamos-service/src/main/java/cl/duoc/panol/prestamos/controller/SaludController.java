package cl.duoc.panol.prestamos.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SaludController {

    @GetMapping("/health")
    public Map<String, String> salud() {
        return Map.of("servicio", "prestamos-service", "estado", "ok");
    }
}
