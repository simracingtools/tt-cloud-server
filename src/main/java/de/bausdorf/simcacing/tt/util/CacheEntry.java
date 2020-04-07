package de.bausdorf.simcacing.tt.util;

import lombok.Getter;

@Getter
public class CacheEntry<T> {
	private long creationTime;
	private T content;

	public CacheEntry(T content) {
		this.creationTime = System.currentTimeMillis();
		this.content = content;
	}
}
