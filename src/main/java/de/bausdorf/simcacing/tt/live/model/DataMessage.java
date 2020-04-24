package de.bausdorf.simcacing.tt.live.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataMessage<T> {
    T message;
}
