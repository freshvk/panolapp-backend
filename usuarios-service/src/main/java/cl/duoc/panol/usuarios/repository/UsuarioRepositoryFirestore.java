package cl.duoc.panol.usuarios.repository;

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

import cl.duoc.panol.usuarios.entities.Usuario;
import cl.duoc.panol.usuarios.exception.ApiException;

@Repository
public class UsuarioRepositoryFirestore implements UsuarioRepository {

    private static final String COLECCION = "usuarios";

    private final Firestore db;

    public UsuarioRepositoryFirestore(Firestore db) {
        this.db = db;
    }

    private CollectionReference coleccion() {
        return db.collection(COLECCION);
    }

    @Override
    public List<Usuario> listar() {
        try {
            List<Usuario> lista = new ArrayList<>();
            for (QueryDocumentSnapshot doc : coleccion().get().get().getDocuments()) {
                lista.add(aUsuario(doc));
            }
            return lista;
        } catch (InterruptedException | ExecutionException e) {
            throw error(e);
        }
    }

    @Override
    public Optional<Usuario> obtenerPorId(Long id) {
        try {
            DocumentSnapshot doc = coleccion().document(String.valueOf(id)).get().get();
            return doc.exists() ? Optional.of(aUsuario(doc)) : Optional.empty();
        } catch (InterruptedException | ExecutionException e) {
            throw error(e);
        }
    }

    @Override
    public Optional<Usuario> obtenerPorCorreo(String correo) {
        try {
            List<QueryDocumentSnapshot> docs = coleccion()
                .whereEqualTo("correo", correo)
                .limit(1)
                .get()
                .get()
                .getDocuments();
            return docs.isEmpty() ? Optional.empty() : Optional.of(aUsuario(docs.get(0)));
        } catch (InterruptedException | ExecutionException e) {
            throw error(e);
        }
    }

    @Override
    public Usuario crear(String nombre, String correo, String telefono, String rol) {
        try {
            Long id = db.runTransaction((Transaction.Function<Long>) tx -> {
                long nuevo = Contadores.siguiente(tx, db, COLECCION);
                DocumentReference ref = coleccion().document(String.valueOf(nuevo));
                tx.set(ref, datos(nuevo, nombre, correo, telefono, rol));
                return Long.valueOf(nuevo);
            }).get();
            return new Usuario(id, nombre, correo, telefono, rol);
        } catch (InterruptedException | ExecutionException e) {
            throw error(e);
        }
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        try {
            coleccion().document(String.valueOf(usuario.getId()))
                .set(datos(usuario.getId(), usuario.getNombre(), usuario.getCorreo(),
                    usuario.getTelefono(), usuario.getRol()))
                .get();
            return usuario;
        } catch (InterruptedException | ExecutionException e) {
            throw error(e);
        }
    }

    private Map<String, Object> datos(long id, String nombre, String correo, String telefono, String rol) {
        Map<String, Object> mapa = new HashMap<>();
        mapa.put("id", id);
        mapa.put("nombre", nombre);
        mapa.put("correo", correo);
        mapa.put("telefono", telefono);
        mapa.put("rol", rol);
        return mapa;
    }

    private Usuario aUsuario(DocumentSnapshot doc) {
        Long id = doc.getLong("id");
        return new Usuario(
            id == null ? Long.valueOf(doc.getId()) : id,
            doc.getString("nombre"),
            doc.getString("correo"),
            doc.getString("telefono"),
            doc.getString("rol"));
    }

    private ApiException error(Exception e) {
        if (e instanceof InterruptedException) {
            Thread.currentThread().interrupt();
        }
        return new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "No se pudo acceder a la base de datos");
    }
}
