package de.bausdorf.simcacing.tt.web;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class Message {
    public static final String ERROR="error";
    public static final String WARN="warning";
    public static final String INFO="info";

    String type;
    String text;
}
