package de.bausdorf.simcacing.tt.web.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class PlanDescriptionView {
	private String id;
	private String name;
	private String team;
}
