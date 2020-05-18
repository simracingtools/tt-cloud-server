package de.bausdorf.simcacing.tt.web.security;

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
