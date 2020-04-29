package de.bausdorf.simcacing.tt.live.model.live;

public enum StateClassType {
	INFO("bg-info"),
	SUCCESS("bg-success"),
	WARNING("bg-warning"),
	ERROR("bg-error");

	private String cssClass;

	StateClassType(String cssClass) {
		this.cssClass = cssClass;
	}

	public String cssClass() {
		return cssClass;
	}
}
