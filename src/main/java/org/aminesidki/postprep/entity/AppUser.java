package org.aminesidki.postprep.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.aminesidki.postprep.enumeration.Role;

import java.util.UUID;
import java.util.List;

@Entity
@Getter
@Setter
public class AppUser {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private UUID id;

    @NonNull
    private String username;
    @NonNull
    private String email;
    @NonNull
    private String password;
    @NonNull
    private Role role;
    @NonNull
    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Article> articles;

    @Column(length = 1024)
    private String refreshToken;
}
