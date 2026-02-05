package org.aminesidki.postprep.repository.projection;

import org.aminesidki.postprep.enumeration.Role;
import java.util.UUID;

public interface AuthInfo {
    UUID getId();
    String getEmail();
    String getPassword();
    Role getRole();
}