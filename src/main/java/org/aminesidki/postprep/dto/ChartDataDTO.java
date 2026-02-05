package org.aminesidki.postprep.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChartDataDTO {
    private String label;
    private Long value;
}