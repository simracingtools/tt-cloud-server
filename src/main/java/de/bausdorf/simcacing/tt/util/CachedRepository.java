package de.bausdorf.simcacing.tt.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class CachedRepository<T> {

	protected Map<String, CacheEntry<T>> cache;
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
		} catch (InterruptedException e) {
			log.warn(e.getMessage());
			Thread.currentThread().interrupt();
			return Optional.empty();
		} catch (ExecutionException e) {
			log.warn(e.getMessage());
			return Optional.empty();
		}
	}

	public void save(String collectionName, String name, T object) {
		firestore.save(collectionName, name, toMap(object));
		putToCache(name, object);
	}

	public void delete(String collectionName, String name) {
		firestore.delete(collectionName, name);
		removeFromCache(name);
	}
	
	protected void putToCache(String key, T object) {
		cache.put(key, new CacheEntry<>(object));
	}

	protected Optional<T> getFromCache(String key) {
		if( cache.containsKey(key) ) {
			return Optional.ofNullable(cache.get(key).getContent());
		}
		return Optional.empty();
	}

	protected void removeFromCache(String key) {
		if( cache.containsKey(key) ) {
			cache.remove(key);
		}
	}

	protected Optional<T> findByName(String collectionName, String key) {
		if( cache.containsKey(key) ) {
			return getFromCache(key);
		}
		Optional<T> fromDb = load(collectionName, key);
		if( fromDb.isPresent() ) {
			putToCache(key, fromDb.get());
		}
		return fromDb;
	}
}
