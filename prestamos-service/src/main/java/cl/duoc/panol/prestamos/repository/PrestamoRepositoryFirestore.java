package cl.duoc.panol.prestamos.repository;

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

import cl.duoc.panol.prestamos.entities.Prestamo;
import cl.duoc.panol.prestamos.exception.ApiException;
import cl.duoc.panol.prestamos.services.Reglas;

@Repository
public class PrestamoRepositoryFirestore implements PrestamoRepository {

    private static final String COLECCION = "prestamos";
    private static final String COLECCION_EQUIPOS = "equipos";

    private final Firestore db;

    public PrestamoRepositoryFirestore(Firestore db) {
        this.db = db;
    }

    private CollectionReference coleccion() {
        return db.collection(COLECCION);
    }

    @Override
    public Prestamo crear(Long equipoId, String solicitante, String usuarioCorreo, int cantidad,
                          long fechaPrestamo, long fechaLimite) {
        try {
            return db.runTransaction((Transaction.Function<Prestamo>) tx -> {
                DocumentReference equipoRef = db.collection(COLECCION_EQUIPOS).document(String.valueOf(equipoId));
                DocumentSnapshot equipo = tx.get(equipoRef).get();
                if (!equipo.exists()) {
                    throw new ApiException(HttpStatus.NOT_FOUND, "El equipo no existe");
                }
                int total = numero(equipo, "cantidadTotal");
                int prestadas = numero(equipo, "prestadas");
                Reglas.validarStock(total, prestadas, cantidad);

                long nuevo = Contadores.siguiente(tx, db, COLECCION);
                String nombreEquipo = equipo.getString("nombre");

                Map<String, Object> cambiosEquipo = new HashMap<>();
                cambiosEquipo.put("prestadas", (long) (prestadas + cantidad));
                tx.update(equipoRef, cambiosEquipo);

                Prestamo prestamo = new Prestamo(nuevo, equipoId, nombreEquipo, usuarioCorreo, solicitante,
                    cantidad, fechaPrestamo, fechaLimite, null, Prestamo.ACTIVO);
                tx.set(coleccion().document(String.valueOf(nuevo)), datos(prestamo));
                return prestamo;
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            throw error(e);
        }
    }

    @Override
    public List<Prestamo> listar() {
        try {
            return aLista(coleccion().get().get().getDocuments());
        } catch (InterruptedException | ExecutionException e) {
            throw error(e);
        }
    }

    @Override
    public List<Prestamo> listarPorUsuario(String usuarioCorreo) {
        try {
            return aLista(coleccion().whereEqualTo("usuarioCorreo", usuarioCorreo).get().get().getDocuments());
        } catch (InterruptedException | ExecutionException e) {
            throw error(e);
        }
    }

    @Override
    public Optional<Prestamo> obtener(Long id) {
        try {
            DocumentSnapshot doc = coleccion().document(String.valueOf(id)).get().get();
            return doc.exists() ? Optional.of(aPrestamo(doc)) : Optional.empty();
        } catch (InterruptedException | ExecutionException e) {
            throw error(e);
        }
    }

    @Override
    public Prestamo devolver(Long id, long fechaDevolucion) {
        try {
            return db.runTransaction((Transaction.Function<Prestamo>) tx -> {
                DocumentReference ref = coleccion().document(String.valueOf(id));
                DocumentSnapshot doc = tx.get(ref).get();
                if (!doc.exists()) {
                    throw new ApiException(HttpStatus.NOT_FOUND, "El préstamo no existe");
                }
                Prestamo prestamo = aPrestamo(doc);
                if (!Prestamo.ACTIVO.equals(prestamo.getEstado())) {
                    throw new ApiException(HttpStatus.CONFLICT, "El préstamo ya fue devuelto");
                }
                DocumentReference equipoRef = db.collection(COLECCION_EQUIPOS)
                    .document(String.valueOf(prestamo.getEquipoId()));
                DocumentSnapshot equipo = tx.get(equipoRef).get();

                Map<String, Object> cambios = new HashMap<>();
                cambios.put("estado", Prestamo.DEVUELTO);
                cambios.put("fechaDevolucion", fechaDevolucion);
                tx.update(ref, cambios);

                if (equipo.exists()) {
                    int prestadas = Math.max(0, numero(equipo, "prestadas") - prestamo.getCantidad());
                    Map<String, Object> cambiosEquipo = new HashMap<>();
                    cambiosEquipo.put("prestadas", (long) prestadas);
                    tx.update(equipoRef, cambiosEquipo);
                }

                prestamo.setEstado(Prestamo.DEVUELTO);
                prestamo.setFechaDevolucion(fechaDevolucion);
                return prestamo;
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            throw error(e);
        }
    }

    private Map<String, Object> datos(Prestamo p) {
        Map<String, Object> mapa = new HashMap<>();
        mapa.put("id", p.getId());
        mapa.put("equipoId", p.getEquipoId());
        mapa.put("equipoNombre", p.getEquipoNombre());
        mapa.put("usuarioCorreo", p.getUsuarioCorreo());
        mapa.put("solicitante", p.getSolicitante());
        mapa.put("cantidad", (long) p.getCantidad());
        mapa.put("fechaPrestamo", p.getFechaPrestamo());
        mapa.put("fechaLimite", p.getFechaLimite());
        mapa.put("fechaDevolucion", p.getFechaDevolucion());
        mapa.put("estado", p.getEstado());
        return mapa;
    }

    private List<Prestamo> aLista(List<QueryDocumentSnapshot> docs) {
        List<Prestamo> lista = new ArrayList<>();
        for (QueryDocumentSnapshot doc : docs) {
            lista.add(aPrestamo(doc));
        }
        return lista;
    }

    private int numero(DocumentSnapshot doc, String campo) {
        Long valor = doc.getLong(campo);
        return valor == null ? 0 : valor.intValue();
    }

    private Prestamo aPrestamo(DocumentSnapshot doc) {
        Long id = doc.getLong("id");
        Long fechaPrestamo = doc.getLong("fechaPrestamo");
        Long fechaLimite = doc.getLong("fechaLimite");
        return new Prestamo(
            id == null ? Long.valueOf(doc.getId()) : id,
            doc.getLong("equipoId"),
            doc.getString("equipoNombre"),
            doc.getString("usuarioCorreo"),
            doc.getString("solicitante"),
            numero(doc, "cantidad"),
            fechaPrestamo == null ? 0L : fechaPrestamo,
            fechaLimite == null ? 0L : fechaLimite,
            doc.getLong("fechaDevolucion"),
            doc.getString("estado"));
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
