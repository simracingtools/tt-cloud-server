package de.bausdorf.simcacing.tt.util;

import java.util.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class CachedRepository<T> extends SimpleRepository<T> {

	protected Map<String, CacheEntry<T>> cache;

	public CachedRepository(FirestoreDB db) {
		super(db);
		cache = new HashMap<>();
	}

	@Override
	public void save(String collectionName, String name, T object) {
		super.save(collectionName, name, object);
		putToCache(name, object);
	}

	@Override
	public void delete(String collectionName, String name) {
		super.delete(collectionName, name);
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

	public void flushCache() {
		cache.clear();
	}

	@Override
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
