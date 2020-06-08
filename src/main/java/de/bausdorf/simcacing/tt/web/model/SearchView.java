package de.bausdorf.simcacing.tt.web.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class SearchView {
	private String userName;
	private String email;
	private String userRole;
	private boolean enabled;
	private boolean locked;
	private boolean expired;

	public SearchView() {
		userName="";
		email = "";
		userRole = "any";
		enabled = false;
		locked = false;
		expired = false;
	}
}
