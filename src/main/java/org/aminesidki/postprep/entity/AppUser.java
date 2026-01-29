package org.aminesidki.postprep.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.UUID;

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

    @ManyToOne
    @NonNull
    private Role role;
}
