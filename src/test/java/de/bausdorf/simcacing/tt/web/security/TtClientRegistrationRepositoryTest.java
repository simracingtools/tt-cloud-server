package de.bausdorf.simcacing.tt.web.security;

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
