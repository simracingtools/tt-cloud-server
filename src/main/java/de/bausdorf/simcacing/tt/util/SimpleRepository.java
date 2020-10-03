package de.bausdorf.simcacing.tt.util;

/*-
 * #%L
 * tt-cloud-server
 * %%
 * Copyright (C) 2020 bausdorf engineering
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class SimpleRepository<T> {
	private MongoDB mongo;

	protected abstract T fromMap(Map<String, Object> data);
	protected abstract Map<String, Object> toMap(T object);

	public SimpleRepository(MongoDB db) {
		this.mongo = db;
	}

	public Optional<T> load(String collectionName, String name) {
		if(StringUtils.isEmpty(collectionName) || StringUtils.isEmpty(name) ) {
			return Optional.empty();
		}
		Document doc = mongo.getDocumentById(collectionName, name);

		return Optional.ofNullable(fromMap(doc));

	}

	public List<T> loadAll(String collectionName) {
		FindIterable<Document> list = mongo.loadAll(collectionName);
		List<T> objectList = new ArrayList<>();
		for( Document docSnap : list ) {
			objectList.add(fromMap(docSnap));
		}
		return objectList;
	}

	public void save(String collectionName, String name, T object) {
		mongo.save(collectionName, name, toMap(object));
	}

	public void update(String collectionName, String documentName, Map<String, Object> updates) {
		mongo.updateDocument(collectionName, documentName, updates);
	}

	public void delete(String collectionName, String name) {
		mongo.delete(collectionName, name);
	}

	protected Optional<T> findByName(String collectionName, String key) {
		return load(collectionName, key);
	}

	public List<T> findByFieldValue(String collectionName, String fieldName, String fieldValue) {
		FindIterable<Document> list = mongo.findByFieldValue(collectionName, fieldName, fieldValue);
		List<T> objectList = new ArrayList<>();
		for( Document docSnap : list ) {
			objectList.add(fromMap(docSnap));
		}
		return objectList;
	}

	public List<T> findByArrayContains(String collectionName, String fieldName, String fieldValue) {
		List<T> objectList = new ArrayList<>();
		if (fieldName != null && fieldValue != null) {
			FindIterable<Document> list = mongo.findByArrayContains(collectionName, fieldName, fieldValue);
			for (Document docSnap : list) {
				objectList.add(fromMap(docSnap));
			}
		}
		return objectList;
	}

	public MongoCollection<Document> getCollectionReference(String collectionName) {
		return mongo.getCollectionReference(collectionName);
	}

	public List<T> findByQuery(Bson query) {
		String collectionName = "Users";
		List<T> objectList = new ArrayList<>();
		if (query != null) {
			FindIterable<Document> list = mongo.findByQuery(collectionName, query);
			for (Document docSnap : list) {
				objectList.add(fromMap(docSnap));
			}
		}
		return objectList;
	}
}
