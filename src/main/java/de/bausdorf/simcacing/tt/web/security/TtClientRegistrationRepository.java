package de.bausdorf.simcacing.tt.web.security;

import com.google.api.client.util.Value;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import de.bausdorf.simcacing.tt.impl.FirestoreDB;
import de.bausdorf.simcacing.tt.impl.TeamtacticsServerProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Component
@Slf4j
public class TtClientRegistrationRepository {

    private String userCollectionName;

    private FirestoreDB firestore;

    public TtClientRegistrationRepository(@Autowired FirestoreDB db, @Autowired TeamtacticsServerProperties config) {
        this.firestore = db;
        this.userCollectionName = config.getUserCollectionName();
    }

    public Optional<TtUser> findById(String userId) {
        ApiFuture<DocumentSnapshot> docRef = firestore.getDocumentById(userCollectionName, userId);
        try {
            if (docRef.get().exists()) {
                TtUser user = new TtUser(docRef.get());
                return Optional.ofNullable(user);
            }
        } catch (InterruptedException | ExecutionException e) {
            log.warn("userid {} could not be queried", userId);
        }
        return Optional.empty();
    }

    public List<TtUser> findByIracingId(String iRacingId) {
        List<QueryDocumentSnapshot> docList = firestore.findByFieldValue(userCollectionName,
                "iRacingId", iRacingId);
        return docList.stream()
                .map(s -> new TtUser(s))
                .collect(Collectors.toList());
    }

    public List<TtUser> findByAccessToken(String tokenValue) {
        List<QueryDocumentSnapshot> docList = firestore.findByFieldValue(userCollectionName,
                "clientMessageAccessToken", tokenValue);
        return docList.stream()
                .map(s -> new TtUser(s))
                .collect(Collectors.toList());
    }

    public void save(TtUser user) {
        firestore.save(userCollectionName, user.getId(), user.toObjectMap());
    }

    public void delete(String userId) {
        firestore.delete(userCollectionName, userId);
    }
}
