package de.bausdorf.simcacing.tt.util;

import java.util.Optional;

import lombok.Getter;
import lombok.Setter;

public abstract class TimeCachedRepository<T> extends CachedRepository<T> {

	@Getter
	@Setter
	private long cachedDurationMillis;

	public TimeCachedRepository(FirestoreDB db, long cachedDurationMillis) {
		super(db);
		this.cachedDurationMillis = cachedDurationMillis;
	}

	@Override
	protected Optional<T> getFromCache(String key) {
		if( cache.containsKey(key) ) {
			CacheEntry<T> hit = cache.get(key);
			if( (hit.getCreationTime() + cachedDurationMillis) < System.currentTimeMillis()) {
				cache.remove(key);
			}
			return Optional.ofNullable(hit.getContent());
		}
		return Optional.empty();
	}
}
