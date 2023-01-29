package de.bausdorf.simcacing.tt;

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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestPropertySource("file:./application.properties")
@Slf4j
class TtCloudServerApplicationTests {

	@Test
	void contextLoads() {
		assertTrue(true);
	}


	@Test
	@Disabled("Manual test")
	void listDatastore() {
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
	@Disabled("Manual test")
	void clearDatastore() {
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
