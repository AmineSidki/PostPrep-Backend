package org.aminesidki.postprep.dto;

import org.aminesidki.postprep.enumeration.Status;

import java.util.UUID;

public record LiteArticleDTO(
        UUID id,
        String title,
        UUID owner,
        Status status
){}
