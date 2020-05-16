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

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Slf4j
public class TtClientRegistrationRepositoryTest {

    @Autowired
    TtClientRegistrationRepository userRepository;

    @Test
    @Disabled
    public void saveUser() {
        TtUser user = TtUser.builder()
                .id("testid")
                .userType(TtUserType.TT_NEW)
                .email("email")
                .imageUrl("imageurl")
                .name("name")
                .build();
        userRepository.save(user);

        Optional<TtUser> createdUser = userRepository.findById("testid");
        assertThat(createdUser.isPresent()).isTrue();
        log.info("created user: " + createdUser);
    }

    @Test
    @Disabled
    public void deleteUser() {
        TtUser user = TtUser.builder()
                .id("testid")
                .userType(TtUserType.TT_NEW)
                .email("email")
                .imageUrl("imageurl")
                .name("name")
                .build();
        userRepository.save(user);
        userRepository.delete("testid");

        Optional<TtUser> createdUser = userRepository.findById("testid");
        assertThat(createdUser.isPresent()).isFalse();
    }

    @Test
    @Disabled
    public void findUserBy() {
        TtUser user = TtUser.builder()
                .id("testid")
                .userType(TtUserType.TT_NEW)
                .email("email")
                .imageUrl("imageurl")
                .name("name")
                .iRacingId("47110815")
                .clientMessageAccessToken("token")
                .build();
        userRepository.save(user);

        List<TtUser> createdUser = userRepository.findByIracingId("47110815");
        assertThat(createdUser.size()).isEqualTo(1);
        log.info("created user: " + createdUser.get(0));

        createdUser = userRepository.findByAccessToken("token");
        assertThat(createdUser.size()).isEqualTo(1);
        log.info("created user: " + createdUser.get(0));

        createdUser = userRepository.findByAccessToken("none");
        assertThat(createdUser.isEmpty()).isTrue();
    }

    @Test
    @Disabled
    public void listDatastore() {
        FirestoreOptions firestoreOptions = FirestoreOptions.getDefaultInstance();
        firestoreOptions.toBuilder()
                        .setProjectId("iracing-team-tactics")
                        .build();
        Firestore db = firestoreOptions.getService();
        for(CollectionReference ref : db.listCollections() ) {
            DocumentReference docRef = ref.document();
            log.info(docRef.getPath());
        }
    }

    @Test
    @Disabled
    public void clearDatastore() {
        FirestoreOptions firestoreOptions = FirestoreOptions.getDefaultInstance();
        firestoreOptions.toBuilder()
                .setProjectId("iracing-team-tactics")
                .build();
        Firestore db = firestoreOptions.getService();
        for(CollectionReference ref : db.listCollections() ) {
            log.info("delete all documents in {}", ref.getPath());
            for(DocumentReference docRef: ref.listDocuments()) {
                log.info("delete {}", docRef.getPath());
                docRef.delete();
            }
            ref.document().delete();
        }
    }

}
