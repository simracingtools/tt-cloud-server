package de.bausdorf.simcacing.tt.planning.model;

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
