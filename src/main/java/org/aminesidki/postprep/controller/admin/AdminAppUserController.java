package org.aminesidki.postprep.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.aminesidki.postprep.dto.AppUserDTO;
import org.aminesidki.postprep.service.AppUserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/admin/users")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminAppUserController {
    private final AppUserService appUserService;

    @GetMapping()
    public List<AppUserDTO> getAppUsers() {
        return appUserService.findAll();
    }

    @DeleteMapping("/{id}")
    public void deleteAppUser(@PathVariable UUID id) {
        appUserService.delete(id);
    }

    @GetMapping("/Details/{id}")
    public AppUserDTO getAppUserDetails(@PathVariable UUID id) {
        return appUserService.findById(id);
    }

    @PutMapping("/{id}")
    public AppUserDTO updateAppUser(@PathVariable UUID id,@Valid @RequestBody AppUserDTO appUserDTO) {
        return appUserService.updateUser(id, appUserDTO);
    }
}