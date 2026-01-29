package org.aminesidki.postprep.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

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
    @CreationTimestamp
    private Timestamp createdAt;
}
