package org.aminesidki.postprep.mapper;

import org.aminesidki.postprep.dto.LiteArticleDTO;
import org.aminesidki.postprep.entity.Article;
import org.springframework.stereotype.Component;

@Component
public class LiteArticleMapper {
    public LiteArticleDTO toDto(Article entity) {
        if (entity == null) {
            return null;
        }

        LiteArticleDTO dto = new LiteArticleDTO();

        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setOwner(entity.getOwner().getId());
        dto.setStatus(entity.getStatus());

        return dto;
    }
}
