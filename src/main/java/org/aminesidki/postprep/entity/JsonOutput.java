package org.aminesidki.postprep.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
public class JsonOutput {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private UUID id;

    @NonNull
    private String summary;
    @NonNull
    private String category;
    @NonNull
    private String seoTitle;
    @NonNull
    private Integer confidenceScore;

    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> keywords;
}
