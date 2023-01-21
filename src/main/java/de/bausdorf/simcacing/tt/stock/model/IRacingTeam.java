package de.bausdorf.simcacing.tt.stock.model;

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

import java.util.List;

import lombok.*;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class IRacingTeam {

	public static final String TEAM_NAME = "Name";
	public static final String TEAM_ID = "TeamId";
	public static final String OWNER_ID = "OwnerId";
	public static final String AUTHORIZED_DRIVERS = "AuthorizedDrivers";
	public static final String TEAM_ADMINS = "TeamAdmins";

	@Id
	private String id;
	private String name;
	private String ownerId;

	@ElementCollection
	private List<String> teamAdminIds;

	@ElementCollection
	private List<String> authorizedDriverIds;

	public boolean isOwner(String memberId) {
		if( ownerId != null && memberId != null ) {
			return ownerId.equals(memberId);
		}
		return false;
	}

	public boolean isAdmin(String memberId) {
		return authorizedDriverIds.contains(memberId);
	}
}
