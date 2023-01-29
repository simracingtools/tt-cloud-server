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

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Slf4j
class TtClientRegistrationRepositoryTest {

    @Autowired
    TtIdentityRepository userRepository;

    @Test
    @Disabled("Manual test")
    void saveUser() {
        TtIdentity user = TtIdentity.builder()
                .id("testid")
                .userType(TtUserType.TT_NEW)
                .email("email")
                .imageUrl("imageurl")
                .name("name")
                .build();
        userRepository.save(user);

        Optional<TtIdentity> createdUser = userRepository.findById("testid");
        assertThat(createdUser).isPresent();
        log.info("created user: " + createdUser);
    }

    @Test
    @Disabled("Manual test")
    void deleteUser() {
        TtIdentity user = TtIdentity.builder()
                .id("testid")
                .userType(TtUserType.TT_NEW)
                .email("email")
                .imageUrl("imageurl")
                .name("name")
                .build();
        userRepository.save(user);
        userRepository.deleteById("testid");

        Optional<TtIdentity> createdUser = userRepository.findById("testid");
        assertThat(createdUser).isNotPresent();
    }

    @Test
    @Disabled("Manual test")
    void findUserBy() {
        TtIdentity user = TtIdentity.builder()
                .id("testid")
                .userType(TtUserType.TT_NEW)
                .email("email")
                .imageUrl("imageurl")
                .name("name")
                .iracingId("47110815")
                .clientMessageAccessToken("token")
                .build();
        userRepository.save(user);

        Optional<TtIdentity> createdUser = userRepository.findByIracingId("47110815");
        assertThat(createdUser).isPresent();
        log.info("created user: " + createdUser.get());

        createdUser = userRepository.findByClientMessageAccessToken("token");
        assertThat(createdUser).isPresent();
        log.info("created user: " + createdUser.get());

        createdUser = userRepository.findByClientMessageAccessToken("none");
        assertThat(createdUser).isNotPresent();
    }
}
