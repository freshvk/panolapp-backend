package cl.duoc.panol.reportes.config;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

@Configuration
public class FirestoreConfig {

    @Bean
    public Firestore firestore(
            @Value("${firebase.credentials-path:}") String ruta,
            @Value("${firebase.credentials-base64:}") String base64) throws IOException {

        GoogleCredentials credenciales;
        if (base64 != null && !base64.isBlank()) {
            byte[] json = Base64.getDecoder().decode(base64.trim());
            try (InputStream in = new ByteArrayInputStream(json)) {
                credenciales = GoogleCredentials.fromStream(in);
            }
        } else if (ruta != null && !ruta.isBlank()) {
            try (InputStream in = new FileInputStream(ruta)) {
                credenciales = GoogleCredentials.fromStream(in);
            }
        } else {
            throw new IllegalStateException(
                "Falta la credencial de Firebase: define FIREBASE_CREDENTIALS_PATH o FIREBASE_CREDENTIALS_BASE64");
        }

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseOptions opciones = FirebaseOptions.builder()
                .setCredentials(credenciales)
                .build();
            FirebaseApp.initializeApp(opciones);
        }
        return FirestoreClient.getFirestore();
    }
}
