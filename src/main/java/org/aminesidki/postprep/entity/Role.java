package org.aminesidki.postprep.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class Role {
    @Id
    private Long id;
    @NonNull
    private String roleTitle;
    @OneToMany
    private List<AppUser> users;
}
