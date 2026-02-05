package org.aminesidki.postprep.repository;

import org.aminesidki.postprep.dto.LiteArticleDTO;
import org.aminesidki.postprep.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface LiteArticleRepository extends JpaRepository<LiteArticleDTO , UUID> {
    @Query(value = "SELECT id , title , owner.id , status FROM Article WHERE owner = :user")
    List<LiteArticleDTO> findByOwner(@Param("user") AppUser user);
}
