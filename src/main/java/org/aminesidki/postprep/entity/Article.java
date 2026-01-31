package org.aminesidki.postprep.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
@Entity
public class Article {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private UUID id;

    @NonNull
    private String title;
    @NonNull
    private String content;
    @NonNull
    private String language;
    @ManyToOne
    private AppUser owner;

    @CreationTimestamp
    private Timestamp createdAt;

    @JdbcTypeCode(SqlTypes.JSON)
    private OutputJson outputJson;
}
