package de.bausdorf.simcacing.tt.util;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import de.bausdorf.simcacing.tt.live.impl.FirestoreException;

@Component
public class FirestoreDB {
    public static final String FIRESTORE_PROJECT_ID = "iracing-team-tactics";

    Firestore firestore;

    public FirestoreDB() {
        FirestoreOptions firestoreOptions = FirestoreOptions.getDefaultInstance();
        firestoreOptions.toBuilder()
                .setProjectId(FIRESTORE_PROJECT_ID)
                .build();
        firestore = firestoreOptions.getService();
    }

    public ApiFuture<DocumentSnapshot> getDocumentById(String collectionName, String documentName) {
        return firestore.collection(collectionName).document(documentName).get();
    }

    public List<QueryDocumentSnapshot> loadAll(String collectionName) {
        ApiFuture<QuerySnapshot> future = firestore.collection(collectionName).get();
        try {
            return future.get().getDocuments();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new FirestoreException(e.getMessage(), e);
        } catch (ExecutionException e) {
            throw new FirestoreException(e.getMessage(), e);
        }
    }

    public List<QueryDocumentSnapshot> findByFieldValue(String collectionName, String fieldName, String fieldValue) {
        CollectionReference colRef = firestore.collection(collectionName);
        Query query = colRef.whereEqualTo(fieldName, fieldValue);
        ApiFuture<QuerySnapshot> future = query.get();
        try {
            return future.get().getDocuments();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new FirestoreException(e.getMessage(), e);
        } catch (ExecutionException e) {
            throw new FirestoreException(e.getMessage(), e);
        }
    }

    public List<QueryDocumentSnapshot> findByArrayContains(String collectionName, String fieldName, String fieldValue) {
        CollectionReference colRef = firestore.collection(collectionName);
        Query query = colRef.whereArrayContains(fieldName, fieldValue);
        ApiFuture<QuerySnapshot> future = query.get();
        try {
            return future.get().getDocuments();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new FirestoreException(e.getMessage(), e);
        } catch (ExecutionException e) {
            throw new FirestoreException(e.getMessage(), e);
        }
    }

    public Timestamp updateDocument(String collectionName, String documentName, Map<String, Object> updates) {
        ApiFuture<WriteResult> future = firestore.collection(collectionName).document(documentName).update(updates);
        try {
            WriteResult writeResult = future.get();
            return writeResult.getUpdateTime();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new FirestoreException(e.getMessage(), e);
        } catch (ExecutionException e) {
            throw new FirestoreException(e.getMessage(), e);
        }
    }

    public Timestamp save(String collectionName, String documentName, Map<String, Object> documentData) {
        ApiFuture<WriteResult> future = firestore.collection(collectionName).document(documentName).set(documentData);
        try {
            WriteResult writeResult = future.get();
            return writeResult.getUpdateTime();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new FirestoreException(e.getMessage(), e);
        } catch (ExecutionException e) {
            throw new FirestoreException(e.getMessage(), e);
        }
    }

    public Timestamp delete(String collectionName, String documentName) {
        ApiFuture<WriteResult> future = firestore.collection(collectionName).document(documentName).delete();
        try {
            WriteResult writeResult = future.get();
            return writeResult.getUpdateTime();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new FirestoreException(e.getMessage(), e);
        } catch (ExecutionException e) {
            throw new FirestoreException(e.getMessage(), e);
        }
    }
}
