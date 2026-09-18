package com.coachly.adminpanel.agegroup;

import com.coachly.adminpanel.agegroup.dto.AgeGroupResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/age-group")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN') || hasRole('MANAGER')")
public class AgeGroupController {

    private final AgeGroupService ageGroupService;

    @GetMapping("/all")
    public List<AgeGroupResponse> getAllAgeGroups() {
        return ageGroupService.getAllAgeGroups();
    }

    @GetMapping("/{id}")
    public AgeGroupResponse getAgeGroup(@PathVariable UUID id) {
        return ageGroupService.getAgeGroup(id);
    }
}
