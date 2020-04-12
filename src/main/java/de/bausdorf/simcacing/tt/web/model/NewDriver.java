package de.bausdorf.simcacing.tt.web.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewDriver {
    private String name;
    private String id;
    private boolean teamAdmin;
    private String teamId;
}
