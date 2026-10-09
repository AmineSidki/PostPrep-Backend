package org.aminesidki.postprep.dto.regular;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

public record ChartDataDTO (
        String label,
        Long value
) implements Serializable {}