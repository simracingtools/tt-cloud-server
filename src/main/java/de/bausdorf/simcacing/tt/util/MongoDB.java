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

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.stereotype.Component;
import org.w3c.dom.css.DocumentCSS;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.ServerAddress;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;

import org.bson.Document;
import java.util.Arrays;

import com.mongodb.BasicDBObject;
import com.mongodb.Block;

import com.mongodb.client.MongoCursor;
import static com.mongodb.client.model.Filters.*;
import com.mongodb.client.result.DeleteResult;
import static com.mongodb.client.model.Updates.*;
import com.mongodb.client.result.UpdateResult;
import java.util.ArrayList;
import java.util.List;

import java.util.Arrays;

@Component
public class MongoDB  {
    public static final String MONGO_PROJECT_ID = "iracing-team-tactics";

    MongoClient mongoClient;
    MongoDatabase mongo;

    public MongoDB() {
        mongoClient = MongoClients.create("mongodb://127.0.0.1:27017");
        mongo = mongoClient.getDatabase("Users");
    }

    public Document getDocumentById(String collectionName, String documentName) {
         
        MongoCollection<Document> myCollection = mongo.getCollection(collectionName);
        Document document = myCollection.find(eq("_id", documentName)).first();
        return document;
    }

    public FindIterable<Document> loadAll(String collectionName) {
        MongoCollection<Document> myCollection  = mongo.getCollection(collectionName);
        FindIterable<Document> list = myCollection.find();
        return list;
    }

    public FindIterable<Document> findByFieldValue(String collectionName, String fieldName, String fieldValue) {
        MongoCollection<Document> myCollection  = mongo.getCollection(collectionName);
        FindIterable<Document> list = myCollection.find(eq(fieldName, fieldValue));
        return list;
    }

    public FindIterable<Document> findByArrayContains(String collectionName, String fieldName, String fieldValue) {
        MongoCollection<Document> myCollection  = mongo.getCollection(collectionName);
        FindIterable<Document> list = myCollection.find(in(fieldName, fieldValue));
        return list;
    }

    public MongoCollection<Document> getCollectionReference(String collectionName) {
        return mongo.getCollection(collectionName);
    }

    public FindIterable<Document> findByQuery(String collectionName, Bson filter) {
        FindIterable<Document> list = mongo.getCollection(collectionName).find(filter);
        return list;
    }

    public UpdateResult updateDocument(String collectionName, String documentName, Map<String, Object> updates) {
        MongoCollection<Document> gradesCollection = mongo.getCollection(collectionName);

        BasicDBObject updateObject = new BasicDBObject();
        updateObject.put("$set", new BasicDBObject(updates)); // (3)
        Bson filter = eq("_id", documentName);
        UpdateResult updateResult = gradesCollection.updateOne(filter, updateObject);
        return updateResult;
    }

    public UpdateResult save(String collectionName, String documentName, Map<String, Object> mapData) {
        //mapData.values().removeIf(Objects::isNull);

        Document documentData = new Document();
        for (Map.Entry<String, Object> entry : mapData.entrySet()) {
            documentData.put(entry.getKey(), entry.getValue());
        }
        
        Bson filter = Filters.eq("_id", documentName);

        BasicDBObject updateObject = new BasicDBObject();
        updateObject.put("$set", documentData); // (3)

        UpdateOptions options = new UpdateOptions().upsert(true);

        UpdateResult updateResult = mongo.getCollection(collectionName).updateOne( filter, updateObject, options);
        return updateResult;
    }

    public DeleteResult delete(String collectionName, String documentName) {
        DeleteResult dr = mongo.getCollection(collectionName).deleteOne(eq("_id", documentName));
        return dr;
    }




}
