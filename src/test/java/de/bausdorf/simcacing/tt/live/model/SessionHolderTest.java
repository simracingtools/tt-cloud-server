package de.bausdorf.simcacing.tt.live.model;

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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import de.bausdorf.simcacing.tt.live.impl.SessionHolder;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
public class SessionHolderTest {

	@Autowired
	SessionHolder container;

	Map<String, Object> sessionMessage;

	@BeforeEach
	void createSessionMessage() {
		sessionMessage = new HashMap<>();
		sessionMessage.put("maxFuel", 120.0D);
		sessionMessage.put("sessionTime", "0.0792385731");
		sessionMessage.put("sessionLaps", "unlimited");
	}

}
