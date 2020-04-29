package de.bausdorf.simcacing.tt.web.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class SessionView {

	private String selectedSession;
	private String selectedPlanId;
	private List<SessionIdentifierView> sessions;

	public SessionView() {
		this.sessions = new ArrayList<>();
	}
}
