package cl.duoc.panol.equipos.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.Transaction;

import cl.duoc.panol.equipos.entities.Equipo;
import cl.duoc.panol.equipos.exception.ApiException;
import cl.duoc.panol.equipos.services.Reglas;

@Repository
public class EquipoRepositoryFirestore implements EquipoRepository {

    private static final String COLECCION = "equipos";

    private final Firestore db;

    public EquipoRepositoryFirestore(Firestore db) {
        this.db = db;
    }

    private CollectionReference coleccion() {
        return db.collection(COLECCION);
    }

    @Override
    public List<Equipo> listar() {
        try {
            List<Equipo> lista = new ArrayList<>();
            for (QueryDocumentSnapshot doc : coleccion().get().get().getDocuments()) {
                lista.add(aEquipo(doc));
            }
            return lista;
        } catch (InterruptedException | ExecutionException e) {
            throw error(e);
        }
    }

    @Override
    public Optional<Equipo> obtener(Long id) {
        try {
            DocumentSnapshot doc = coleccion().document(String.valueOf(id)).get().get();
            return doc.exists() ? Optional.of(aEquipo(doc)) : Optional.empty();
        } catch (InterruptedException | ExecutionException e) {
            throw error(e);
        }
    }

    @Override
    public Equipo crear(String nombre, String categoria, int cantidadTotal, int stockMinimo) {
        try {
            Long id = db.runTransaction((Transaction.Function<Long>) tx -> {
                long nuevo = Contadores.siguiente(tx, db, COLECCION);
                DocumentReference ref = coleccion().document(String.valueOf(nuevo));
                tx.set(ref, datos(nuevo, nombre, categoria, cantidadTotal, stockMinimo, 0));
                return Long.valueOf(nuevo);
            }).get();
            return new Equipo(id, nombre, categoria, cantidadTotal, stockMinimo, 0);
        } catch (InterruptedException | ExecutionException e) {
            throw error(e);
        }
    }

    @Override
    public Equipo actualizar(Long id, String nombre, String categoria, int cantidadTotal, int stockMinimo) {
        try {
            return db.runTransaction((Transaction.Function<Equipo>) tx -> {
                DocumentReference ref = coleccion().document(String.valueOf(id));
                DocumentSnapshot doc = tx.get(ref).get();
                if (!doc.exists()) {
                    throw new ApiException(HttpStatus.NOT_FOUND, "El equipo no existe");
                }
                int prestadas = numero(doc, "prestadas");
                Reglas.validarCantidadTotal(prestadas, cantidadTotal);
                Map<String, Object> cambios = new HashMap<>();
                cambios.put("nombre", nombre);
                cambios.put("categoria", categoria);
                cambios.put("cantidadTotal", (long) cantidadTotal);
                cambios.put("stockMinimo", (long) stockMinimo);
                tx.update(ref, cambios);
                return new Equipo(id, nombre, categoria, cantidadTotal, stockMinimo, prestadas);
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            throw error(e);
        }
    }

    @Override
    public void eliminar(Long id) {
        try {
            db.runTransaction((Transaction.Function<Boolean>) tx -> {
                DocumentReference ref = coleccion().document(String.valueOf(id));
                DocumentSnapshot doc = tx.get(ref).get();
                if (!doc.exists()) {
                    throw new ApiException(HttpStatus.NOT_FOUND, "El equipo no existe");
                }
                Reglas.validarEliminable(numero(doc, "prestadas"));
                tx.delete(ref);
                return Boolean.TRUE;
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            throw error(e);
        }
    }

    @Override
    public boolean estaVacio() {
        try {
            return coleccion().limit(1).get().get().isEmpty();
        } catch (InterruptedException | ExecutionException e) {
            throw error(e);
        }
    }

    private Map<String, Object> datos(long id, String nombre, String categoria, int total, int minimo, int prestadas) {
        Map<String, Object> mapa = new HashMap<>();
        mapa.put("id", id);
        mapa.put("nombre", nombre);
        mapa.put("categoria", categoria);
        mapa.put("cantidadTotal", (long) total);
        mapa.put("stockMinimo", (long) minimo);
        mapa.put("prestadas", (long) prestadas);
        return mapa;
    }

    private int numero(DocumentSnapshot doc, String campo) {
        Long valor = doc.getLong(campo);
        return valor == null ? 0 : valor.intValue();
    }

    private Equipo aEquipo(DocumentSnapshot doc) {
        Long id = doc.getLong("id");
        return new Equipo(
            id == null ? Long.valueOf(doc.getId()) : id,
            doc.getString("nombre"),
            doc.getString("categoria"),
            numero(doc, "cantidadTotal"),
            numero(doc, "stockMinimo"),
            numero(doc, "prestadas"));
    }

    private ApiException error(Exception e) {
        Throwable causa = e.getCause();
        while (causa != null) {
            if (causa instanceof ApiException api) {
                return api;
            }
            causa = causa.getCause();
        }
        if (e instanceof InterruptedException) {
            Thread.currentThread().interrupt();
        }
        return new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "No se pudo acceder a la base de datos");
    }
}
