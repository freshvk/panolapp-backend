package cl.duoc.panol.equipos.repository;

import java.util.Map;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Transaction;

public final class Contadores {

    private Contadores() {
    }

    public static long siguiente(Transaction tx, Firestore db, String nombre) throws Exception {
        DocumentReference ref = db.collection("contadores").document(nombre);
        DocumentSnapshot snap = tx.get(ref).get();
        Long actual = snap.exists() ? snap.getLong("valor") : null;
        long siguiente = (actual == null ? 0L : actual) + 1L;
        tx.set(ref, Map.of("valor", siguiente));
        return siguiente;
    }
}
