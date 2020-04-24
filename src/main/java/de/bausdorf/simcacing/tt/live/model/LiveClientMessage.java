package de.bausdorf.simcacing.tt.live.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LiveClientMessage {
    private String teamId;
    private String text;
}
