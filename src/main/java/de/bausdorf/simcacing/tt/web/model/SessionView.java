package de.bausdorf.simcacing.tt.web.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.Data;

@Data
public class SessionView {

	private Integer selectedSessionIndex;
	private String selectedPlanId;
	private List<SessionIdentifierView> sessions;

	public SessionView() {
		this.sessions = new ArrayList<>();
	}

	public Optional<SessionIdentifierView> getSelectedSession() {
		if (selectedSessionIndex < sessions.size()) {
			return Optional.of(sessions.get(selectedSessionIndex));
		}
		return Optional.empty();
	}
}
