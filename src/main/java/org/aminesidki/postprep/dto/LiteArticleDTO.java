package org.aminesidki.postprep.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.aminesidki.postprep.enumeration.Status;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiteArticleDTO {
    private UUID id;
    private String title;
    private UUID owner;
    private Status status;
}
