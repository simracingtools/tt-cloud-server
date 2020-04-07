package de.bausdorf.simcacing.tt.stock;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;

import de.bausdorf.simcacing.tt.FirestoreDB;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class CachedRepository<T> {

	protected Map<String, T> cache;
	private FirestoreDB firestore;

	abstract protected T fromMap(Map<String, Object> data);
	abstract protected Map<String, Object> toMap(T object);

	public CachedRepository(FirestoreDB db) {
		cache = new HashMap<>();
		this.firestore = db;
	}

	public Optional<T> load(String collectionName, String name) {
		ApiFuture<DocumentSnapshot> carDoc = firestore.getDocumentById(collectionName, name);
		DocumentSnapshot docSnap = null;
		try {
			docSnap = carDoc.get();
			return Optional.ofNullable(fromMap(docSnap.getData()));
		} catch (InterruptedException | ExecutionException e) {
			log.warn(e.getMessage());
			return Optional.empty();
		}
	}

	public void save(String collectionName, String name, T object) {
		firestore.save(collectionName, name, toMap(object));
		cache.put(name, object);
	}

	protected Optional<T> findByName(String collectionName, String key) {
		if( cache.containsKey(key) ) {
			return Optional.of(cache.get(key));
		}
		Optional<T> fromDb = load(collectionName, key);
		if( fromDb.isPresent() ) {
			cache.put(key, fromDb.get());
		}
		return fromDb;
	}
}
