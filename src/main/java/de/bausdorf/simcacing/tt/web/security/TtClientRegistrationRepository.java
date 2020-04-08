package de.bausdorf.simcacing.tt.web.security;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import de.bausdorf.simcacing.tt.util.FirestoreDB;
import de.bausdorf.simcacing.tt.util.TeamtacticsServerProperties;
import de.bausdorf.simcacing.tt.util.TimeCachedRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Component
@Slf4j
public class TtClientRegistrationRepository extends TimeCachedRepository<TtUser> {

    private String userCollectionName;

    private FirestoreDB firestore;

    public TtClientRegistrationRepository(@Autowired FirestoreDB db, @Autowired TeamtacticsServerProperties config) {
        super(db, config.getUserRepositoryCacheMinutes());
        this.userCollectionName = config.getUserCollectionName();
    }

    @Override
    protected TtUser fromMap(Map<String, Object> data) {
        if( data == null ) {
            return null;
        }
        return new TtUser(data);
    }

    @Override
    protected Map<String, Object> toMap(TtUser object) {
        return object.toObjectMap();
    }

    public Optional<TtUser> findById(String userId) {
        return super.findByName(userCollectionName, userId);
    }

    public List<TtUser> findByIracingId(String iRacingId) {
        List<QueryDocumentSnapshot> docList = firestore.findByFieldValue(userCollectionName,
                "iRacingId", iRacingId);
        return docList.stream()
                .map(s -> new TtUser(s.getData()))
                .collect(Collectors.toList());
    }

    public List<TtUser> findByAccessToken(String tokenValue) {
        List<QueryDocumentSnapshot> docList = firestore.findByFieldValue(userCollectionName,
                "clientMessageAccessToken", tokenValue);
        return docList.stream()
                .map(s -> new TtUser(s.getData()))
                .collect(Collectors.toList());
    }

    public List<TtUser> findByUserEmail(String email) {
        List<QueryDocumentSnapshot> docList = firestore.findByFieldValue(userCollectionName,
                "email", email);
        return docList.stream()
                .map(s -> new TtUser(s.getData()))
                .collect(Collectors.toList());
    }

    public void save(TtUser user) {
        super.save(userCollectionName, user.getId(), user);
    }

    public void delete(String userId) {
        super.delete(userCollectionName, userId);
    }

}
