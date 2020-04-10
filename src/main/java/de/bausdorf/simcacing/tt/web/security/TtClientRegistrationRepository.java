package de.bausdorf.simcacing.tt.web.security;

import de.bausdorf.simcacing.tt.util.FirestoreDB;
import de.bausdorf.simcacing.tt.util.TeamtacticsServerProperties;
import de.bausdorf.simcacing.tt.util.TimeCachedRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class TtClientRegistrationRepository extends TimeCachedRepository<TtUser> {

    private final String userCollectionName;

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
        return super.findByFieldValue(userCollectionName,
                "iRacingId", iRacingId);
    }

    public List<TtUser> findByAccessToken(String tokenValue) {
        return super.findByFieldValue(userCollectionName,
                "clientMessageAccessToken", tokenValue);
    }

    public List<TtUser> findByUserEmail(String email) {
        return super.findByFieldValue(userCollectionName,
                "email", email);
    }

    public void save(TtUser user) {
        super.save(userCollectionName, user.getId(), user);
    }

    public void delete(String userId) {
        super.delete(userCollectionName, userId);
    }

}
