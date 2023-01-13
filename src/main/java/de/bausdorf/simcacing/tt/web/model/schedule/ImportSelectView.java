package de.bausdorf.simcacing.tt.web.model.schedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImportSelectView {
    @Data
    @AllArgsConstructor
    public static class SelectView {
        private String name;
        private Long id;
    }
    private String title;
    private List<SelectView> series;
    private List<Long> selectedSeries;
}
