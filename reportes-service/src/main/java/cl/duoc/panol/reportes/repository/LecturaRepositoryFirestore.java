package cl.duoc.panol.reportes.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;

import cl.duoc.panol.reportes.entities.EquipoDato;
import cl.duoc.panol.reportes.entities.PrestamoDato;
import cl.duoc.panol.reportes.exception.ApiException;

@Repository
public class LecturaRepositoryFirestore implements LecturaRepository {

    private final Firestore db;

    public LecturaRepositoryFirestore(Firestore db) {
        this.db = db;
    }

    @Override
    public List<EquipoDato> equipos() {
        try {
            List<EquipoDato> lista = new ArrayList<>();
            for (QueryDocumentSnapshot doc : db.collection("equipos").get().get().getDocuments()) {
                lista.add(new EquipoDato(
                    doc.getLong("id"),
                    doc.getString("nombre"),
                    numero(doc, "cantidadTotal"),
                    numero(doc, "stockMinimo"),
                    numero(doc, "prestadas")));
            }
            return lista;
        } catch (InterruptedException | ExecutionException e) {
            throw error(e);
        }
    }

    @Override
    public List<PrestamoDato> prestamos() {
        try {
            return aLista(db.collection("prestamos").get().get().getDocuments());
        } catch (InterruptedException | ExecutionException e) {
            throw error(e);
        }
    }

    @Override
    public List<PrestamoDato> prestamosDe(String usuarioCorreo) {
        try {
            return aLista(db.collection("prestamos")
                .whereEqualTo("usuarioCorreo", usuarioCorreo)
                .get().get().getDocuments());
        } catch (InterruptedException | ExecutionException e) {
            throw error(e);
        }
    }

    private List<PrestamoDato> aLista(List<QueryDocumentSnapshot> docs) {
        List<PrestamoDato> lista = new ArrayList<>();
        for (QueryDocumentSnapshot doc : docs) {
            Long limite = doc.getLong("fechaLimite");
            lista.add(new PrestamoDato(
                doc.getLong("id"),
                doc.getLong("equipoId"),
                doc.getString("equipoNombre"),
                doc.getString("usuarioCorreo"),
                numero(doc, "cantidad"),
                limite == null ? 0L : limite,
                doc.getString("estado")));
        }
        return lista;
    }

    private int numero(DocumentSnapshot doc, String campo) {
        Long valor = doc.getLong(campo);
        return valor == null ? 0 : valor.intValue();
    }

    private ApiException error(Exception e) {
        if (e instanceof InterruptedException) {
            Thread.currentThread().interrupt();
        }
        return new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "No se pudo acceder a la base de datos");
    }
}
