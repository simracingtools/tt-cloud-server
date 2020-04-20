package de.bausdorf.simcacing.tt.planning.model;

import lombok.Getter;

@Getter
public enum ScheduleDriverOptionType {
    OPEN("bg-success"),
    TENTATIVE("bg-warning"),
    BLOCKED("bg-danger"),
    UNSCHEDULED("bg-info");

    private String cssClassName;

    private ScheduleDriverOptionType(String cssClass) {
        this.cssClassName = cssClass;
    }

    public static ScheduleDriverOptionType fromCssClass(String className) {
        switch (className) {
            case "bg-success": return OPEN;
            case "bg-warning": return TENTATIVE;
            case "bg-danger" : return BLOCKED;
            case "bg-info"   : return UNSCHEDULED;
            default: throw new IllegalArgumentException("Css class name unknown: " + className);
        }
    }

    public String cssClassName() {
        return cssClassName;
    }
}
