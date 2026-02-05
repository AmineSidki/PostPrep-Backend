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

        return new LiteArticleDTO(entity.getId(),
                                                entity.getTitle(),
                                                entity.getOwner().getId(),
                                                entity.getStatus());

    }
}
